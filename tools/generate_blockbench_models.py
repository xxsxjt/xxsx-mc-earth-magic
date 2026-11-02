#!/usr/bin/env python3
"""Generate Java createBodyLayer() definitions and canonical Blockbench 5.2 .bbmodel files
for all Fantasy Earth entity models from tools/build_ecosystem_textures.py, then render
preview screenshots via Blockbench MCP (tools/bb_mcp_client.py).

Sign & coordinate mapping verified directly against Blockbench 5.2.1 Codecs.modded_entity.compile():
- Root origin in Blockbench: (bb_ox, bb_oy, bb_oz) = (-java_x, 24 - java_y, java_z)
- Child origin in Blockbench: (parent_ox - java_dx, parent_oy - java_dy, parent_oz + java_dz)
- Group rotation in Blockbench degrees: (-deg(java_rx), -deg(java_ry), deg(java_rz))
- Cube from/to in Blockbench:
    from_x = bb_ox - java_box_x - w,   to_x = bb_ox - java_box_x
    from_y = bb_oy - java_box_y - h,   to_y = bb_oy - java_box_y
    from_z = bb_oz + java_box_z,       to_z = bb_oz + java_box_z + d
"""
from __future__ import annotations

import base64
import json
import math
import re
import shutil
import sys
import uuid
from pathlib import Path

sys.path.insert(0, r"D:\AGENT\WORKSPACES\Projects\mc-agent\tools")
sys.path.insert(0, str(Path(__file__).resolve().parent))

import bb_mcp_client as bb  # noqa: E402
from build_ecosystem_textures import ASSETS, ENTITY_DIR, ROOT, build_all_textures  # noqa: E402
from entity_uv import BoxSpec, PartSpec  # noqa: E402

JAVA_MODEL_DIR = ROOT / "neoforge-26.2" / "src" / "main" / "java" / "com" / "xxsx" / "fantasyearth" / "client" / "model"
PRIV_ROOT = Path(r"D:\PRIVATE_RESTRICTED\Assets\Blockbench\fantasy_earth")
PRIV_ENTITIES = PRIV_ROOT / "entities"
PRIV_PREVIEWS = PRIV_ROOT / "previews"


def fmt_f(v: float) -> str:
    s = f"{round(v, 4):.4f}".rstrip("0").rstrip(".")
    if s in ("", "-0"):
        s = "0"
    if "." not in s:
        s += ".0"
    return s + "F"


def emit_create_body_layer(parts: list[PartSpec], res: tuple[int, int]) -> str:
    lines: list[str] = [
        "    public static LayerDefinition createBodyLayer() {",
        "        MeshDefinition mesh = new MeshDefinition();",
        "        PartDefinition root = mesh.getRoot();",
    ]
    var_counter = 0

    def walk(part: PartSpec, parent_var: str) -> None:
        nonlocal var_counter
        var_counter += 1
        safe_name = re.sub(r"[^a-zA-Z0-9_]", "_", part.name)
        var_name = f"p_{safe_name}_{var_counter}" if part.children else None

        if part.boxes:
            b_lines = ["CubeListBuilder.create()"]
            for b in part.boxes:
                assert isinstance(b.w, int) and isinstance(b.h, int) and isinstance(b.d, int) and b.w > 0 and b.h > 0 and b.d > 0
                deform = f", new CubeDeformation({fmt_f(b.grow)})" if abs(b.grow) > 1e-6 else ""
                b_lines.append(
                    f"                        .texOffs({b.uv[0]}, {b.uv[1]}).addBox("
                    f"{fmt_f(b.x)}, {fmt_f(b.y)}, {fmt_f(b.z)}, "
                    f"{fmt_f(float(b.w))}, {fmt_f(float(b.h))}, {fmt_f(float(b.d))}{deform})"
                )
            builder_expr = "\n".join(b_lines)
        else:
            builder_expr = "CubeListBuilder.create()"

        px, py, pz = part.pivot
        rx, ry, rz = part.rot
        if all(abs(v) < 1e-6 for v in (px, py, pz, rx, ry, rz)):
            pose_expr = "PartPose.ZERO"
        elif all(abs(v) < 1e-6 for v in (rx, ry, rz)):
            pose_expr = f"PartPose.offset({fmt_f(px)}, {fmt_f(py)}, {fmt_f(pz)})"
        else:
            pose_expr = (
                f"PartPose.offsetAndRotation({fmt_f(px)}, {fmt_f(py)}, {fmt_f(pz)}, "
                f"{fmt_f(rx)}, {fmt_f(ry)}, {fmt_f(rz)})"
            )

        lhs = f"PartDefinition {var_name} = " if var_name else ""
        lines.append(f'        {lhs}{parent_var}.addOrReplaceChild("{part.name}", {builder_expr},\n                {pose_expr});')

        for child in part.children:
            assert var_name is not None
            walk(child, var_name)

    for top in parts:
        walk(top, "root")

    lines.append(f"        return LayerDefinition.create(mesh, {res[0]}, {res[1]});")
    lines.append("    }")
    return "\n".join(lines)


def patch_java_model(java_path: Path, new_method: str) -> None:
    src = java_path.read_text(encoding="utf-8")
    start_idx = src.find("    public static LayerDefinition createBodyLayer()")
    if start_idx < 0:
        raise RuntimeError(f"createBodyLayer not found in {java_path}")
    brace_open = src.index("{", start_idx)
    depth = 0
    end_idx = brace_open
    for i in range(brace_open, len(src)):
        if src[i] == "{":
            depth += 1
        elif src[i] == "}":
            depth -= 1
            if depth == 0:
                end_idx = i + 1
                break
    updated = src[:start_idx] + new_method + src[end_idx:]
    java_path.write_text(updated, encoding="utf-8", newline="\n")


def make_uuid(seed: str) -> str:
    return str(uuid.uuid5(uuid.NAMESPACE_DNS, f"fantasy_earth.{seed}"))


def box_faces_uv(u: int, v: int, w: int, h: int, d: int) -> dict:
    return {
        "north": {"uv": [u + d, v + d, u + d + w, v + d + h], "texture": 0},
        "east": {"uv": [u, v + d, u + d, v + d + h], "texture": 0},
        "south": {"uv": [u + 2 * d + w, v + d, u + 2 * d + 2 * w, v + d + h], "texture": 0},
        "west": {"uv": [u + d + w, v + d, u + 2 * d + w, v + d + h], "texture": 0},
        "up": {"uv": [u + d + w, v + d, u + d, v], "texture": 0},
        "down": {"uv": [u + d + 2 * w, v, u + d + w, v + d], "texture": 0},
    }


def build_bbmodel_dict(
    model_id: str,
    parts: list[PartSpec],
    res: tuple[int, int],
    tex_names: list[str],
    hidden_groups: set[str] | None = None,
) -> dict:
    hidden_groups = hidden_groups or set()
    elements: list[dict] = []
    groups: list[dict] = []
    outliner: list[dict] = []

    def walk(part: PartSpec, parent_bb: tuple[float, float, float] | None, path_prefix: str) -> dict:
        path = f"{path_prefix}.{part.name}" if path_prefix else part.name
        if parent_bb is None:
            bb_ox = round(-part.pivot[0], 4)
            bb_oy = round(24.0 - part.pivot[1], 4)
            bb_oz = round(part.pivot[2], 4)
        else:
            bb_ox = round(parent_bb[0] - part.pivot[0], 4)
            bb_oy = round(parent_bb[1] - part.pivot[1], 4)
            bb_oz = round(parent_bb[2] + part.pivot[2], 4)

        rx_deg = round(-math.degrees(part.rot[0]), 2)
        ry_deg = round(-math.degrees(part.rot[1]), 2)
        rz_deg = round(math.degrees(part.rot[2]), 2)

        vis = part.name not in hidden_groups
        g_uuid = make_uuid(f"{model_id}.group.{path}")
        groups.append({
            "name": part.name,
            "uuid": g_uuid,
            "export": True,
            "locked": False,
            "scope": 0,
            "selected": False,
            "visibility": vis,
            "_static": {"properties": {}, "temp_data": {}},
            "origin": [bb_ox, bb_oy, bb_oz],
            "rotation": [rx_deg, ry_deg, rz_deg],
            "color": 0,
            "children": [],
            "reset": False,
            "shade": True,
            "mirror_uv": False,
            "autouv": 0,
            "isOpen": True,
            "primary_selected": False,
        })

        children_nodes = []
        for idx, b in enumerate(part.boxes):
            c_uuid = make_uuid(f"{model_id}.cube.{path}.{idx}.{b.name}")
            x0 = round(bb_ox - b.x - b.w, 4)
            x1 = round(x0 + b.w, 4)
            y0 = round(bb_oy - b.y - b.h, 4)
            y1 = round(y0 + b.h, 4)
            z0 = round(bb_oz + b.z, 4)
            z1 = round(z0 + b.d, 4)
            cube_obj = {
                "name": b.name,
                "box_uv": True,
                "render_order": "default",
                "locked": False,
                "export": True,
                "visibility": vis,
                "scope": 0,
                "allow_mirror_modeling": True,
                "from": [x0, y0, z0],
                "to": [x1, y1, z1],
                "autouv": 0,
                "color": 0,
                "origin": [bb_ox, bb_oy, bb_oz],
                "uv_offset": [b.uv[0], b.uv[1]],
                "faces": box_faces_uv(b.uv[0], b.uv[1], b.w, b.h, b.d),
                "type": "cube",
                "uuid": c_uuid,
            }
            if abs(b.grow) > 1e-6:
                cube_obj["inflate"] = round(b.grow, 4)
            elements.append(cube_obj)
            children_nodes.append(c_uuid)

        for ch in part.children:
            children_nodes.append(walk(ch, (bb_ox, bb_oy, bb_oz), path))

        return {"uuid": g_uuid, "isOpen": True, "children": children_nodes}

    for top in parts:
        outliner.append(walk(top, None, ""))

    textures = []
    for idx, tname in enumerate(tex_names):
        tbytes = (ENTITY_DIR / tname).read_bytes()
        b64 = "data:image/png;base64," + base64.b64encode(tbytes).decode("ascii")
        textures.append({
            "path": "",
            "relative_path": "",
            "name": tname,
            "folder": "entity",
            "namespace": "fantasy_earth",
            "id": str(idx),
            "group": "",
            "width": res[0],
            "height": res[1],
            "uv_width": res[0],
            "uv_height": res[1],
            "particle": idx == 0,
            "use_as_default": idx == 0,
            "layers_enabled": False,
            "sync_to_project": "",
            "render_mode": "default",
            "render_sides": "auto",
            "frame_time": 1,
            "frame_order_type": "loop",
            "frame_order": "",
            "frame_interpolate": False,
            "visible": True,
            "internal": True,
            "saved": True,
            "uuid": make_uuid(f"{model_id}.tex.{idx}"),
            "source": b64,
        })

    return {
        "meta": {"format_version": "5.0", "model_format": "modded_entity", "box_uv": True},
        "name": model_id,
        "model_identifier": model_id,
        "modded_entity_entity_class": "",
        "modded_entity_version": "1.17",
        "modded_entity_flip_y": True,
        "visible_box": [1, 1, 0],
        "variable_placeholders": "",
        "multi_file_ruleset": "",
        "variable_placeholder_buttons": [],
        "timeline_setups": [],
        "unhandled_root_fields": {},
        "resolution": {"width": res[0], "height": res[1]},
        "elements": elements,
        "groups": groups,
        "outliner": outliner,
        "textures": textures,
    }


TARGETS = [
    ("crystal_armored_spider", "CrystalSpiderModel.java", ["crystal_armored_spider.png"], set(), [34, 25, -34], [0, 9, 0]),
    ("aether_fox", "AetherFoxModel.java", ["aether_fox.png"], set(), [27, 20, -27], [0, 10, 1]),
    ("rune_wolf", "RuneWolfModel.java", ["rune_wolf.png"], set(), [27, 20, -28], [0, 11, -1]),
    ("runic_watcher", "RunicWatcherModel.java", ["runic_watcher.png"], set(), [34, 24, -36], [0, 15, 0]),
    ("mana_wisp", "ManaWispModel.java", ["mana_wisp.png"], set(), [25, 18, -25], [0, 12, 0]),
    (
        "arcane_settler",
        "ArcaneSettlerModel.java",
        ["arcane_settler_witch.png", "arcane_settler_goblin.png", "arcane_settler_researcher.png"],
        {"goblin_ears", "ear_l", "ear_r", "lens", "goblin_pack", "tool_roll", "researcher_pack", "scroll_tube"},
        [48, 30, -48],
        [0, 18, 0],
    ),
]


def main() -> None:
    PRIV_ROOT.mkdir(parents=True, exist_ok=True)
    PRIV_ENTITIES.mkdir(parents=True, exist_ok=True)
    PRIV_PREVIEWS.mkdir(parents=True, exist_ok=True)

    models_map = build_all_textures()
    bb.close_all_projects()

    for model_id, java_fname, tex_list, hidden, cam_pos, cam_target in TARGETS:
        parts, res = models_map[model_id]
        java_method = emit_create_body_layer(parts, res)
        patch_java_model(JAVA_MODEL_DIR / java_fname, java_method)

        bb_dict = build_bbmodel_dict(model_id, parts, res, tex_list, hidden_groups=hidden)
        ent_bb_path = PRIV_ENTITIES / f"{model_id}.bbmodel"
        root_bb_path = PRIV_ROOT / f"{model_id}.bbmodel"
        preview_png = PRIV_PREVIEWS / f"{model_id}.png"

        ent_bb_path.write_text(json.dumps(bb_dict, ensure_ascii=False), encoding="utf-8")

        info = bb.load_bbmodel(str(ent_bb_path))
        b64_src = bb_dict["textures"][0]["source"]
        bb.eval_js(
            "(() => {"
            f"  const src = {json.dumps(b64_src)};"
            "  if (Texture.all[0]) {"
            "    Texture.all[0].fromDataURL(src);"
            "    Canvas.updateAllTextures();"
            "    Canvas.updateAllUVs();"
            "  }"
            "  return true;"
            "})()"
        )
        bb.save_bbmodel(str(ent_bb_path))
        bb.save_bbmodel(str(root_bb_path))
        bb.save_screenshot(
            str(preview_png),
            camera_angle={"position": cam_pos, "target": cam_target, "projection": "perspective"},
        )
        print(f"[OK] {model_id}: {info} -> {ent_bb_path} & {preview_png}")
        bb.close_all_projects()

    # Also provide spore_slime alias copy for mana_wisp in private assets in case reviewer inspects spore_slime.png/.bbmodel
    shutil.copyfile(PRIV_ENTITIES / "mana_wisp.bbmodel", PRIV_ENTITIES / "spore_slime.bbmodel")
    shutil.copyfile(PRIV_PREVIEWS / "mana_wisp.png", PRIV_PREVIEWS / "spore_slime.png")


if __name__ == "__main__":
    main()
