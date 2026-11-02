#!/usr/bin/env python3
"""Procedural UV allocator and pixel-art material shader library for Fantasy Earth entities.

Strictly forbids 1x1 checkerboards ((x+y)%2) and harsh cartoon black borders.
Every box face is painted with volumetric lighting, directional fur/cloth strokes,
curved chitin carapace speculars, multi-facet crystal refraction, or soft rune glow.
"""
from __future__ import annotations

import math
from dataclasses import dataclass, field
from pathlib import Path
from typing import Callable, Optional

from PIL import Image, ImageDraw


def hex_rgb(value: str) -> tuple[int, int, int]:
    value = value.removeprefix("#")
    return (int(value[0:2], 16), int(value[2:4], 16), int(value[4:6], 16))


def clamp_byte(v: float) -> int:
    return max(0, min(255, int(round(v))))


def rgba(rgb: tuple[int, int, int], delta: float = 0.0, alpha: int = 255) -> tuple[int, int, int, int]:
    return (clamp_byte(rgb[0] + delta), clamp_byte(rgb[1] + delta), clamp_byte(rgb[2] + delta), alpha)


def lerp_rgb(c0: tuple[int, int, int], c1: tuple[int, int, int], t: float) -> tuple[int, int, int]:
    t = max(0.0, min(1.0, t))
    return (
        clamp_byte(c0[0] + (c1[0] - c0[0]) * t),
        clamp_byte(c0[1] + (c1[1] - c0[1]) * t),
        clamp_byte(c0[2] + (c1[2] - c0[2]) * t),
    )


FACE_LIGHT = {
    "up": 18.0,
    "down": -22.0,
    "north": 9.0,
    "east": 5.0,
    "west": -4.0,
    "south": -10.0,
}


@dataclass
class BoxSpec:
    name: str
    x: float
    y: float
    z: float
    w: int
    h: int
    d: int
    style: str = "fur"
    base: Optional[str] = None
    accent: Optional[str] = None
    grow: float = 0.0
    alpha: int = 255
    uv: tuple[int, int] = (0, 0)
    motif: Optional[str] = None


@dataclass
class PartSpec:
    name: str
    pivot: tuple[float, float, float]
    rot: tuple[float, float, float] = (0.0, 0.0, 0.0)
    boxes: list[BoxSpec] = field(default_factory=list)
    children: list["PartSpec"] = field(default_factory=list)


class UVAllocator:
    def __init__(self, width: int, height: int) -> None:
        self.width = width
        self.height = height
        self.cx = 0
        self.cy = 0
        self.row_h = 0

    def allocate(self, w: int, h: int, d: int) -> tuple[int, int]:
        assert w >= 1 and h >= 1 and d >= 1, f"Non-positive integer box dims: {(w, h, d)}"
        bw = 2 * (w + d) + 1
        bh = d + h + 1
        if self.cx + bw > self.width:
            self.cx = 0
            self.cy += self.row_h
            self.row_h = 0
        if self.cy + bh > self.height:
            raise RuntimeError(f"Atlas {self.width}x{self.height} overflow at box ({w},{h},{d}) cursor y={self.cy}")
        uv = (self.cx, self.cy)
        self.cx += bw
        self.row_h = max(self.row_h, bh)
        return uv


def face_rects(u: int, v: int, w: int, h: int, d: int) -> dict[str, tuple[int, int, int, int]]:
    # Vanilla / Blockbench modded_entity standard box UV layout:
    # Top row: [u+d .. u+d+w] = up, [u+d+w .. u+d+2w] = down
    # Bottom row: [u .. u+d] = east (right), [u+d .. u+d+w] = north (front),
    #             [u+d+w .. u+2d+w] = west (left), [u+2d+w .. u+2d+2w] = south (back)
    return {
        "up": (u + d, v, u + d + w, v + d),
        "down": (u + d + w, v, u + d + 2 * w, v + d),
        "east": (u, v + d, u + d, v + d + h),
        "north": (u + d, v + d, u + d + w, v + d + h),
        "west": (u + d + w, v + d, u + 2 * d + w, v + d + h),
        "south": (u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h),
    }


def paint_box(
    image: Image.Image,
    box: BoxSpec,
    default_base: tuple[int, int, int],
    default_accent: tuple[int, int, int],
) -> dict[str, tuple[int, int, int, int]]:
    draw = ImageDraw.Draw(image)
    u, v = box.uv
    w, h, d = int(box.w), int(box.h), int(box.d)
    base = hex_rgb(box.base) if box.base else default_base
    accent = hex_rgb(box.accent) if box.accent else default_accent
    alpha = box.alpha
    rects = face_rects(u, v, w, h, d)

    dorsal = tuple(clamp_byte(c * 0.80) for c in base)
    ventral = tuple(clamp_byte(c * 1.26 + 14) for c in base)

    for face, rect in rects.items():
        x0, y0, x1, y1 = rect
        fw = x1 - x0
        fh = y1 - y0
        if fw <= 0 or fh <= 0:
            continue
        fl = FACE_LIGHT[face]

        for py in range(y0, y1):
            ty = (py - y0) / max(1, fh - 1) if fh > 1 else 0.5
            ry = py - y0
            for px in range(x0, x1):
                tx = (px - x0) / max(1, fw - 1) if fw > 1 else 0.5
                rx = px - x0

                if box.style == "fur":
                    if face == "up":
                        grad_c = lerp_rgb(dorsal, base, 0.35 + 0.3 * math.cos((tx - 0.5) * math.pi))
                    elif face == "down":
                        grad_c = ventral
                    else:
                        grad_c = lerp_rgb(dorsal, ventral, ty ** 0.92)
                    cyl = -11.0 * ((2.0 * tx - 1.0) ** 2)
                    # Long sweeping directional hair brush strokes (wavelength ~4-5px, never 1x1)
                    stroke = math.sin(rx * 0.85 + ry * 0.42) * 7.5 + math.cos(ry * 0.95 - rx * 0.3) * 3.5
                    pix = rgba(grad_c, fl + cyl + stroke, alpha)

                elif box.style == "fur_glow":
                    # Smooth transition from deep fur into luminous starlight/rune tip
                    t_glow = (ty * 0.55 + tx * 0.45) if face in ("east", "west", "up") else ty
                    t_glow = max(0.0, min(1.0, t_glow * 1.18))
                    rgb = lerp_rgb(base, accent, t_glow ** 0.85)
                    sheen = math.sin(rx * 0.7 + ry * 0.5) * 6.0 + 30.0 * (t_glow ** 1.6)
                    pix = rgba(rgb, fl * 0.45 + sheen, alpha)

                elif box.style == "chitin":
                    # Curved obsidian/amethyst arthropod carapace with glossy highlight arc
                    arch = math.exp(-(((ty - 0.28) ** 2) / 0.09 + ((tx - 0.42) ** 2) / 0.28)) * 34.0
                    rim = -14.0 * ((2.0 * tx - 1.0) ** 2) - 10.0 * ty
                    plate_wave = math.sin(ry * 1.05) * 4.5
                    rgb = lerp_rgb(base, accent, max(0.0, min(0.42, arch / 85.0)))
                    pix = rgba(rgb, fl + arch + rim + plate_wave, alpha)

                elif box.style == "crystal":
                    # Multi-facet oblique crystal prism refraction (sharp diagonal facet planes + glowing apex)
                    diag = tx * 0.68 + ty * 0.32
                    if diag < 0.36:
                        facet_c = lerp_rgb(accent, (245, 248, 255), (0.36 - diag) * 1.45)
                        facet_d = 24.0
                    elif diag < 0.68:
                        facet_c = lerp_rgb(base, accent, 0.65)
                        facet_d = 4.0
                    else:
                        facet_c = lerp_rgb(base, (28, 18, 52), (diag - 0.68) * 1.4)
                        facet_d = -14.0
                    ridge = max(0.0, 1.0 - abs(tx - 0.36) * 3.2) * 26.0
                    apex = (1.0 - ty) * 20.0
                    pix = rgba(facet_c, fl * 0.35 + facet_d + ridge + apex, alpha)

                elif box.style == "gel":
                    # Translucent/luminous arcane-spore slime jelly with internal caustic gradient
                    radial = math.hypot(tx - 0.45, ty - 0.42)
                    core_mix = max(0.0, 1.0 - radial * 1.35)
                    rgb = lerp_rgb(base, accent, core_mix * 0.65)
                    spec = math.exp(-(((tx - 0.30) ** 2 + (ty - 0.26) ** 2) / 0.045)) * 42.0
                    pix = rgba(rgb, fl * 0.5 + spec - radial * 12.0, alpha)

                elif box.style == "fungal_cap":
                    # Bioluminescent arcane mushroom cap: rich gradient + soft glowing spots
                    rgb = lerp_rgb(accent, base, min(1.0, ty * 0.85 + 0.1))
                    cyl = -10.0 * ((2.0 * tx - 1.0) ** 2)
                    spot_wave = math.sin((rx + 1.5) * 0.95) * math.cos((ry + 1.0) * 0.95)
                    spot_boost = 28.0 if spot_wave > 0.62 else 0.0
                    if spot_boost > 0:
                        rgb = lerp_rgb(rgb, (220, 252, 255), 0.45)
                    pix = rgba(rgb, fl + cyl + spot_boost, alpha)

                elif box.style == "stone_rune":
                    # Carved ancient arcane golem basalt/marble with warm weathered strata
                    strata = math.sin(rx * 0.65 + ry * 1.15) * 9.0 + math.cos(rx * 1.3 - ry * 0.5) * 4.5
                    grad = (0.45 - ty) * 14.0 - 8.0 * ((2.0 * tx - 1.0) ** 2)
                    pix = rgba(base, fl + strata + grad, alpha)

                elif box.style == "metal":
                    # Brushed arcane bronze / rune-steel with horizon specular band
                    spec = math.exp(-((ty - 0.34) ** 2) / 0.065) * 32.0
                    cyl = -9.0 * ((2.0 * tx - 1.0) ** 2)
                    rgb = lerp_rgb(base, accent, 0.18 if spec > 18.0 else 0.0)
                    pix = rgba(rgb, fl + spec + cyl - 4.0, alpha)

                elif box.style == "cloth":
                    # Velvet arcane robe drapery folds + golden/arcane hem border
                    fold = math.sin(rx * 1.15 + ty * 1.7) * 9.5
                    grad = (0.45 - ty) * 14.0 - 7.0 * ((2.0 * tx - 1.0) ** 2)
                    rgb = base
                    if fh >= 4 and ry == fh - 1 and face in ("north", "south", "east", "west"):
                        rgb = accent
                        grad += 18.0
                    pix = rgba(rgb, fl + fold + grad, alpha)

                elif box.style == "skin":
                    cyl = -8.5 * ((2.0 * tx - 1.0) ** 2) + (0.42 - ty) * 10.0
                    pix = rgba(base, fl + cyl, alpha)

                elif box.style == "hair":
                    sheen = math.exp(-((ty - 0.34) ** 2) / 0.07) * 22.0
                    strand = math.sin(rx * 1.55) * 5.5
                    pix = rgba(base, fl + sheen + strand, alpha)

                elif box.style == "leather":
                    grain = math.sin(rx * 1.2 + ry * 0.9) * 5.0 + (0.4 - ty) * 11.0
                    pix = rgba(base, fl + grain, alpha)

                else:
                    pix = rgba(base, fl, alpha)

                draw.point((px, py), fill=pix)

        # Soft bevel (light top edge, soft shadow bottom edge — never black outline)
        if fw >= 5 and fh >= 5 and box.style not in ("crystal", "gel", "fur_glow"):
            for px in range(x0, x1):
                tc = image.getpixel((px, y0))
                bc = image.getpixel((px, y1 - 1))
                draw.point((px, y0), fill=(clamp_byte(tc[0] + 9), clamp_byte(tc[1] + 9), clamp_byte(tc[2] + 9), tc[3]))
                draw.point((px, y1 - 1), fill=(clamp_byte(bc[0] - 10), clamp_byte(bc[1] - 10), clamp_byte(bc[2] - 10), bc[3]))

        # Motifs (glowing runes, eyes, faces) with soft halo blending
        if box.motif:
            _paint_motif(image, draw, rect, face, box.motif, base, accent)

    return rects


def _soft_glow_point(image: Image.Image, px: int, py: int, bounds: tuple[int, int, int, int],
                     core_rgb: tuple[int, int, int], halo_strength: float = 0.45) -> None:
    x0, y0, x1, y1 = bounds
    if x0 <= px < x1 and y0 <= py < y1:
        old = image.getpixel((px, py))
        image.putpixel((px, py), rgba(core_rgb, 24.0, old[3]))
    for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
        nx, ny = px + dx, py + dy
        if x0 <= nx < x1 and y0 <= ny < y1:
            old = image.getpixel((nx, ny))
            blended = lerp_rgb(old[:3], core_rgb, halo_strength)
            image.putpixel((nx, ny), (blended[0], blended[1], blended[2], old[3]))


def _paint_motif(
    image: Image.Image,
    draw: ImageDraw.ImageDraw,
    rect: tuple[int, int, int, int],
    face: str,
    motif: str,
    base: tuple[int, int, int],
    accent: tuple[int, int, int],
) -> None:
    x0, y0, x1, y1 = rect
    fw, fh = x1 - x0, y1 - y0
    cx, cy = x0 + fw // 2, y0 + fh // 2

    if motif == "spider_eyes" and face == "north" and fw >= 6 and fh >= 5:
        # 8 arachnid eyes: 2 large principal eyes + 2 lateral pairs + 2 upper ocelli with cyan/violet glow
        ey = y0 + fh // 2
        glow_cyan = (96, 246, 255)
        glow_violet = (206, 132, 255)
        for ex in (cx - 1, cx):
            _soft_glow_point(image, ex, ey, rect, glow_cyan, 0.4)
        for ex in (cx - 3, cx + 2):
            _soft_glow_point(image, ex, max(y0 + 1, ey - 1), rect, glow_violet, 0.35)
        for ex in (cx - 2, cx + 1):
            if ey - 2 >= y0:
                image.putpixel((ex, ey - 2), rgba(glow_cyan, 10))

    elif motif in ("fox_face", "wolf_face") and face == "north" and fw >= 6 and fh >= 5:
        ey = y0 + max(1, fh // 2)
        lx = x0 + 1
        rx = x1 - 2
        pupil = (20, 18, 34, 255)
        iris = rgba(accent, 38)
        sclera = rgba(lerp_rgb(accent, (255, 255, 255), 0.55), 20)
        # Slanted fierce/mystic eyes
        draw.point((lx, ey), fill=pupil)
        draw.point((lx + 1, ey), fill=iris)
        if ey - 1 >= y0:
            draw.point((lx, ey - 1), fill=sclera)
        draw.point((rx, ey), fill=pupil)
        draw.point((rx - 1, ey), fill=iris)
        if ey - 1 >= y0:
            draw.point((rx, ey - 1), fill=sclera)
        # Forehead arcane diamond sigil
        _soft_glow_point(image, cx, y0 + 1, rect, accent, 0.42)
        if y0 + 2 < y1:
            _soft_glow_point(image, cx, y0 + 2, rect, lerp_rgb(accent, (255, 255, 255), 0.4), 0.30)

    elif motif == "muzzle_nose" and face == "north" and fw >= 2 and fh >= 2:
        nose_c = (24, 22, 32, 255)
        spec_c = (92, 96, 118, 255)
        for px in range(max(x0, cx - 1), min(x1, cx + 1)):
            draw.point((px, y0), fill=nose_c)
        draw.point((max(x0, cx - 1), y0), fill=spec_c)

    elif motif == "flank_rune" and face in ("east", "west") and fw >= 6 and fh >= 4:
        pts = [
            (x0 + 1, cy),
            (x0 + max(2, fw // 3), max(y0 + 1, cy - 1)),
            (cx, cy),
            (x0 + min(fw - 2, (2 * fw) // 3), min(y1 - 2, cy + 1)),
            (x1 - 2, max(y0 + 1, cy - 1)),
        ]
        soft_c = lerp_rgb(base, accent, 0.52)
        for px, py in pts:
            _soft_glow_point(image, px, py, rect, soft_c, 0.35)
        _soft_glow_point(image, cx, cy, rect, accent, 0.50)

    elif motif == "core_sigil" and face in ("north", "south", "east", "west", "up") and fw >= 4 and fh >= 4:
        _soft_glow_point(image, cx, cy, rect, (235, 252, 255), 0.55)
        for dx, dy in ((-1, 0), (1, 0), (0, -1), (0, 1)):
            _soft_glow_point(image, cx + dx, cy + dy, rect, accent, 0.38)

    elif motif == "settler_face_witch" and face == "north":
        _paint_settler_face(draw, rect, hair_hex="3b2838", eye_hex="b46cff", beard=False, goblin=False)
    elif motif == "settler_face_goblin" and face == "north":
        _paint_settler_face(draw, rect, hair_hex="3e3124", eye_hex="f5b841", beard=False, goblin=True)
    elif motif == "settler_face_researcher" and face == "north":
        _paint_settler_face(draw, rect, hair_hex="4e3529", eye_hex="4ee2f5", beard=True, goblin=False)

    elif motif == "robe_front" and face == "north" and fw >= 6 and fh >= 8:
        # Velvet gold-trimmed V-lapel + arcane clasp + belt buckle
        gold_bright = rgba(accent, 26)
        gold_mid = rgba(accent, -4)
        for step in range(4):
            lx = max(x0, cx - 3 + step)
            rx = min(x1 - 1, cx + 2 - step)
            py = min(y1 - 1, y0 + step)
            draw.point((lx, py), fill=gold_bright)
            draw.point((rx, py), fill=gold_bright)
        # Center vertical embroidery
        for py in range(y0 + 3, y1 - 1):
            draw.point((cx - 1, py), fill=gold_mid)
        # Belt & glowing buckle
        by = y0 + int(fh * 0.65)
        belt_leather = rgba((62, 42, 30), 0)
        for px in range(x0, x1):
            draw.point((px, by), fill=belt_leather)
        draw.point((cx - 1, by), fill=gold_bright)
        draw.point((cx, by), fill=rgba((96, 236, 250), 20))


def _paint_settler_face(draw: ImageDraw.ImageDraw, rect: tuple[int, int, int, int],
                        hair_hex: str, eye_hex: str, beard: bool, goblin: bool) -> None:
    x0, y0, x1, y1 = rect
    hair_c = rgba(hex_rgb(hair_hex), -8)
    brow_c = rgba(hex_rgb(hair_hex), -20)
    eye_c = rgba(hex_rgb(eye_hex), 18)
    pupil_c = rgba(hex_rgb(eye_hex), -35)
    white_c = (246, 242, 232, 255)

    # Hairline & sideburns
    for dx in range(8):
        draw.point((x0 + dx, y0), fill=hair_c)
    for dx in (0, 1, 6, 7):
        draw.point((x0 + dx, y0 + 1), fill=hair_c)
    for dx in (0, 7):
        draw.point((x0 + dx, y0 + 2), fill=hair_c)

    # Expressive brows
    for dx in (1, 2, 5, 6):
        draw.point((x0 + dx, y0 + 2), fill=brow_c)

    # Two-pixel eyes (sclera + glowing iris)
    draw.point((x0 + 1, y0 + 3), fill=white_c)
    draw.point((x0 + 2, y0 + 3), fill=eye_c)
    draw.point((x0 + 2, y0 + 4), fill=pupil_c)

    draw.point((x0 + 6, y0 + 3), fill=white_c)
    draw.point((x0 + 5, y0 + 3), fill=eye_c)
    draw.point((x0 + 5, y0 + 4), fill=pupil_c)

    # Nose bridge & soft mouth
    nose_shadow = (148, 102, 82, 255) if not goblin else (82, 108, 56, 255)
    draw.point((x0 + 3, y0 + 4), fill=nose_shadow)
    draw.point((x0 + 4, y0 + 4), fill=nose_shadow)

    if goblin:
        # Mischievous grin with tiny tusk highlight
        mouth_c = (54, 72, 36, 255)
        for dx in (2, 3, 4, 5):
            draw.point((x0 + dx, y0 + 6), fill=mouth_c)
        draw.point((x0 + 2, y0 + 5), fill=(238, 232, 205, 255))
    elif beard:
        for dx, dy in ((1, 6), (2, 6), (3, 6), (4, 6), (5, 6), (6, 6), (2, 7), (3, 7), (4, 7), (5, 7)):
            draw.point((x0 + dx, y0 + dy), fill=hair_c)
        draw.point((x0 + 3, y0 + 5), fill=(166, 112, 92, 255))
        draw.point((x0 + 4, y0 + 5), fill=(166, 112, 92, 255))
    else:
        # Warm witch smile + subtle blush
        draw.point((x0 + 1, y0 + 5), fill=(214, 138, 132, 255))
        draw.point((x0 + 6, y0 + 5), fill=(214, 138, 132, 255))
        draw.point((x0 + 3, y0 + 6), fill=(168, 96, 98, 255))
        draw.point((x0 + 4, y0 + 6), fill=(168, 96, 98, 255))


def allocate_and_paint(
    specs: list[PartSpec],
    atlas_size: tuple[int, int],
    default_base: str,
    default_accent: str,
    out_path: Path,
    post_hook: Optional[Callable[[Image.Image], None]] = None,
) -> Image.Image:
    alloc = UVAllocator(atlas_size[0], atlas_size[1])

    def walk_alloc(p: PartSpec) -> None:
        for b in p.boxes:
            b.uv = alloc.allocate(b.w, b.h, b.d)
        for c in p.children:
            walk_alloc(c)

    for top in specs:
        walk_alloc(top)

    img = Image.new("RGBA", atlas_size, (0, 0, 0, 0))
    b_rgb = hex_rgb(default_base)
    a_rgb = hex_rgb(default_accent)

    def walk_paint(p: PartSpec) -> None:
        for b in p.boxes:
            paint_box(img, b, b_rgb, a_rgb)
        for c in p.children:
            walk_paint(c)

    for top in specs:
        walk_paint(top)

    if post_hook:
        post_hook(img)

    out_path.parent.mkdir(parents=True, exist_ok=True)
    img.save(out_path)
    return img
