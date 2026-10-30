"""Generates the Fantasy Earth particle sprites.

Each sprite is a separate texture because Minecraft's particle json lists
individual sprites from the particle atlas. Run from the repository root:

    python tools/generate_particle_textures.py
"""

from __future__ import annotations

import math
import os

from PIL import Image

OUT_DIR = os.path.join(
    "neoforge-26.2", "src", "main", "resources", "assets", "fantasy_earth",
    "textures", "particle",
)


def new_image(size: int) -> Image.Image:
    return Image.new("RGBA", (size, size), (0, 0, 0, 0))


def mix(a, b, t):
    return tuple(round(a[i] + (b[i] - a[i]) * t) for i in range(3))


def radial(size: int, inner, outer, power: float = 1.6) -> Image.Image:
    image = new_image(size)
    pixels = image.load()
    centre = (size - 1) / 2.0
    radius = size / 2.0
    for y in range(size):
        for x in range(size):
            distance = math.hypot(x - centre, y - centre) / radius
            if distance > 1.0:
                continue
            falloff = (1.0 - distance) ** power
            colour = mix(outer, inner, falloff)
            pixels[x, y] = (*colour, round(255 * min(1.0, falloff * 1.35)))
    return image


def aether_mote() -> Image.Image:
    return radial(8, (240, 252, 255), (86, 208, 255), 1.35)


def crystal_shard() -> Image.Image:
    size = 8
    image = new_image(size)
    pixels = image.load()
    centre = (size - 1) / 2.0
    for y in range(size):
        for x in range(size):
            dx = abs(x - centre) / (size / 2.0)
            dy = abs(y - centre) / (size / 2.0)
            diamond = dx + dy * 1.45
            if diamond > 1.0:
                continue
            falloff = 1.0 - diamond
            colour = mix((92, 176, 236), (236, 250, 255), falloff)
            pixels[x, y] = (*colour, round(255 * min(1.0, 0.35 + falloff * 1.1)))
    return image


def rune_spark() -> Image.Image:
    size = 8
    image = new_image(size)
    pixels = image.load()
    centre = (size - 1) / 2.0
    for y in range(size):
        for x in range(size):
            dx = abs(x - centre)
            dy = abs(y - centre)
            cross = min(dx, dy)
            diagonal = min(abs(dx - dy), 4.0)
            line = min(cross, diagonal * 1.6)
            if line > 0.85:
                continue
            falloff = 1.0 - line / 0.85
            colour = mix((120, 232, 255), (255, 255, 255), falloff)
            pixels[x, y] = (*colour, round(255 * min(1.0, falloff * 1.5)))
    return image


def ward_rune() -> Image.Image:
    size = 16
    image = new_image(size)
    pixels = image.load()
    strokes = [
        ((3.5, 2.0), (3.5, 13.0)),
        ((3.5, 2.0), (8.0, 6.5)),
        ((8.0, 6.5), (12.5, 2.0)),
        ((12.5, 2.0), (12.5, 13.0)),
        ((5.0, 9.5), (11.0, 9.5)),
        ((5.0, 12.0), (11.0, 12.0)),
    ]
    for (x0, y0), (x1, y1) in strokes:
        steps = int(max(abs(x1 - x0), abs(y1 - y0)) * 4) + 1
        for step in range(steps + 1):
            t = step / steps
            px = x0 + (x1 - x0) * t
            py = y0 + (y1 - y0) * t
            for ox in (-1, 0, 1):
                for oy in (-1, 0, 1):
                    x = int(round(px + ox))
                    y = int(round(py + oy))
                    if not (0 <= x < size and 0 <= y < size):
                        continue
                    distance = math.hypot(ox, oy)
                    if distance > 1.5:
                        continue
                    strength = 1.0 - distance / 1.6
                    current = pixels[x, y]
                    if current[3] >= round(255 * strength):
                        continue
                    colour = mix((122, 59, 214), (233, 216, 255), strength)
                    pixels[x, y] = (*colour, round(255 * min(1.0, strength * 1.25)))
    return image


SPRITES = {
    "aether_mote": aether_mote,
    "crystal_shard": crystal_shard,
    "rune_spark": rune_spark,
    "ward_rune": ward_rune,
}


def main() -> None:
    os.makedirs(OUT_DIR, exist_ok=True)
    for name, builder in SPRITES.items():
        image = builder()
        path = os.path.join(OUT_DIR, f"{name}.png")
        image.save(path)
        print(f"wrote {path} ({image.width}x{image.height})")


if __name__ == "__main__":
    main()
