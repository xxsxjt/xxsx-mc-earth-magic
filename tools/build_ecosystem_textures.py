#!/usr/bin/env python3
"""Define all 6 Fantasy Earth entity geometry specs, allocate non-overlapping integer Box UVs,
paint rich shaded textures (no 1x1 checkerboards or dark cartoon outlines), and emit Spawn Egg / Contract icons.

Also provides get_all_models() so tools/generate_blockbench_models.py can emit Java LayerDefinitions
and canonical Blockbench 5.2 .bbmodel files from the single source of truth.
"""
from __future__ import annotations

import copy
from pathlib import Path

from PIL import Image, ImageDraw

from entity_uv import BoxSpec, PartSpec, allocate_and_paint, hex_rgb, lerp_rgb, rgba

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "neoforge-26.2" / "src" / "main" / "resources" / "assets" / "fantasy_earth"
ENTITY_DIR = ASSETS / "textures" / "entity"
ITEM_DIR = ASSETS / "textures" / "item"


# ---------------------------------------------------------------------------
# 1. Crystal Armored Spider (crystal_armored_spider, 128x128)
# ---------------------------------------------------------------------------
def spec_crystal_spider() -> list[PartSpec]:
    head = PartSpec(
        "head",
        (0.0, 15.0, -4.0),
        (0.06, 0.0, 0.0),
        boxes=[
            BoxSpec("cephalothorax", -4.0, -3.0, -5.0, 8, 6, 6, style="chitin", base="241a3d", accent="9d5ce6", motif="spider_eyes"),
            BoxSpec("head_crest", -3.0, -4.0, -4.0, 6, 2, 5, style="chitin", base="342457", accent="b97aff"),
            BoxSpec("mandible_bridge", -3.0, 0.0, -6.5, 6, 3, 2, style="chitin", base="2d204a", accent="5ce8f5"),
        ],
        children=[
            PartSpec(
                "left_fang",
                (2.0, 1.5, -5.5),
                (0.28, -0.32, -0.14),
                boxes=[
                    BoxSpec("fang_base", -1.0, -1.0, -3.0, 2, 3, 3, style="crystal", base="5e2ca8", accent="6af2ff"),
                    BoxSpec("fang_tip", -0.5, 0.5, -5.0, 1, 2, 2, style="crystal", base="4ce0f0", accent="e6ffff"),
                ],
            ),
            PartSpec(
                "right_fang",
                (-2.0, 1.5, -5.5),
                (0.28, 0.32, 0.14),
                boxes=[
                    BoxSpec("fang_base", -1.0, -1.0, -3.0, 2, 3, 3, style="crystal", base="5e2ca8", accent="6af2ff"),
                    BoxSpec("fang_tip", -0.5, 0.5, -5.0, 1, 2, 2, style="crystal", base="4ce0f0", accent="e6ffff"),
                ],
            ),
            PartSpec(
                "head_horn",
                (0.0, -3.5, -2.5),
                (-0.42, 0.0, 0.0),
                boxes=[BoxSpec("horn_prism", -1.0, -4.0, -1.0, 2, 4, 2, style="crystal", base="6f38c2", accent="7bf4ff")],
            ),
        ],
    )

    crystals = PartSpec(
        "crystals",
        (0.0, -4.0, 3.0),
        (0.0, 0.0, 0.0),
        children=[
            PartSpec("spire_main", (0.0, 0.0, 0.0), (-0.24, 0.0, 0.06),
                     boxes=[BoxSpec("prism", -1.5, -8.0, -1.5, 3, 8, 3, style="crystal", base="6e34bf", accent="e2b8ff")]),
            PartSpec("spire_front_l", (2.5, 0.5, -2.0), (0.34, 0.18, 0.46),
                     boxes=[BoxSpec("prism", -1.0, -6.0, -1.0, 2, 6, 2, style="crystal", base="5a2ba6", accent="74f2ff")]),
            PartSpec("spire_front_r", (-2.5, 0.5, -2.0), (0.34, -0.18, -0.46),
                     boxes=[BoxSpec("prism", -1.0, -6.0, -1.0, 2, 6, 2, style="crystal", base="5a2ba6", accent="74f2ff")]),
            PartSpec("spire_mid_l", (3.5, 1.0, 1.5), (-0.12, -0.14, 0.62),
                     boxes=[BoxSpec("prism", -1.0, -6.0, -1.0, 2, 6, 2, style="crystal", base="7b3ed1", accent="eabfff")]),
            PartSpec("spire_mid_r", (-3.5, 1.0, 1.5), (-0.12, 0.14, -0.62),
                     boxes=[BoxSpec("prism", -1.0, -6.0, -1.0, 2, 6, 2, style="crystal", base="7b3ed1", accent="eabfff")]),
            PartSpec("spire_rear_l", (2.0, 1.0, 4.5), (-0.48, -0.22, 0.36),
                     boxes=[BoxSpec("prism", -1.0, -5.0, -1.0, 2, 5, 2, style="crystal", base="52309e", accent="6cf0ff")]),
            PartSpec("spire_rear_r", (-2.0, 1.0, 4.5), (-0.48, 0.22, -0.36),
                     boxes=[BoxSpec("prism", -1.0, -5.0, -1.0, 2, 5, 2, style="crystal", base="52309e", accent="6cf0ff")]),
        ],
    )

    abdomen = PartSpec(
        "abdomen",
        (0.0, 14.5, 1.5),
        (-0.08, 0.0, 0.0),
        boxes=[
            BoxSpec("core", -5.0, -4.0, -2.0, 10, 8, 11, style="chitin", base="251b3f", accent="9d5ce6", motif="flank_rune"),
            BoxSpec("dorsal_shell", -5.5, -4.5, -1.0, 11, 4, 9, style="chitin", base="3e2466", accent="c58aff", grow=0.12),
        ],
        children=[
            PartSpec("plate_left", (4.5, -2.0, 3.0), (0.0, 0.08, 0.38),
                     boxes=[BoxSpec("plate", -1.0, -2.0, -4.5, 2, 5, 9, style="chitin", base="4a2b7a", accent="5ce8f5")]),
            PartSpec("plate_right", (-4.5, -2.0, 3.0), (0.0, -0.08, -0.38),
                     boxes=[BoxSpec("plate", -1.0, -2.0, -4.5, 2, 5, 9, style="chitin", base="4a2b7a", accent="5ce8f5")]),
            PartSpec("tail_stinger", (0.0, -1.0, 8.5), (-0.32, 0.0, 0.0),
                     boxes=[BoxSpec("stinger", -2.0, -2.0, 0.0, 4, 4, 3, style="crystal", base="5b2ea8", accent="7af5ff")]),
            crystals,
        ],
    )

    # 8 radial articulated spider legs:
    # i = 0..3: right side (X = -3.5), boxes extend -X. Femur rises UP (zRot = +0.62), tibia folds outward-down (zRot = -1.16)
    # i = 4..7: left side  (X = +3.5), boxes extend +X. Femur rises UP (zRot = -0.62), tibia folds outward-down (zRot = +1.16)
    # Wide fan-out yaws [-0.88, -0.30, +0.30, +0.88] (~50°, ~17°, -17°, -50°) so all 8 legs spread dramatically!
    row_z = [-3.6, -1.4, 0.8, 3.0]
    right_yaws = [-0.88, -0.30, 0.30, 0.88]
    femur_rolls = [0.56, 0.64, 0.64, 0.56]
    tibia_rolls = [-1.14, -1.20, -1.20, -1.14]
    legs: list[PartSpec] = []
    for i in range(8):
        left = i >= 4
        row = i % 4
        z = row_z[row]
        yaw = -right_yaws[row] if left else right_yaws[row]
        femur_roll = -femur_rolls[row] if left else femur_rolls[row]
        tibia_roll = -tibia_rolls[row] if left else tibia_rolls[row]
        claw_roll = 0.28 if left else -0.28

        if left:
            femur_box = BoxSpec("femur", 0.0, -1.0, -1.0, 7, 2, 2, style="chitin", base="332154", accent="56e5f2")
            crest_box = BoxSpec("femur_crystal", 2.0, -2.0, -0.5, 4, 1, 1, style="crystal", base="6835ba", accent="9cf7ff")
            tibia_box = BoxSpec("tibia", 0.0, -1.0, -1.0, 8, 2, 2, style="chitin", base="44296e", accent="b97aff")
            claw_box = BoxSpec("claw", 0.0, -0.5, -0.5, 3, 1, 1, style="crystal", base="4ce0f0", accent="ebffff")
            x_root, x_knee, x_tip = 3.5, 6.5, 7.5
        else:
            femur_box = BoxSpec("femur", -7.0, -1.0, -1.0, 7, 2, 2, style="chitin", base="332154", accent="56e5f2")
            crest_box = BoxSpec("femur_crystal", -6.0, -2.0, -0.5, 4, 1, 1, style="crystal", base="6835ba", accent="9cf7ff")
            tibia_box = BoxSpec("tibia", -8.0, -1.0, -1.0, 8, 2, 2, style="chitin", base="44296e", accent="b97aff")
            claw_box = BoxSpec("claw", -3.0, -0.5, -0.5, 3, 1, 1, style="crystal", base="4ce0f0", accent="ebffff")
            x_root, x_knee, x_tip = -3.5, -6.5, -7.5

        legs.append(
            PartSpec(
                f"leg_{i}",
                (x_root, 15.5, z),
                (0.0, yaw, femur_roll),
                boxes=[femur_box, crest_box],
                children=[
                    PartSpec(
                        "lower",
                        (x_knee, 0.0, 0.0),
                        (0.0, 0.0, tibia_roll),
                        boxes=[tibia_box],
                        children=[
                            PartSpec("claw", (x_tip, 0.0, 0.0), (0.0, 0.0, claw_roll), boxes=[claw_box]),
                        ],
                    )
                ],
            )
        )

    return [head, abdomen] + legs


# ---------------------------------------------------------------------------
# 2. Aether Fox (aether_fox, 128x128)
# ---------------------------------------------------------------------------
def spec_aether_fox() -> list[PartSpec]:
    body = PartSpec(
        "body",
        (0.0, 14.5, 0.0),
        (-0.05, 0.0, 0.0),
        boxes=[
            BoxSpec("chest", -3.5, -3.5, -5.5, 7, 7, 6, style="fur", base="26325c", accent="a77dff", motif="flank_rune"),
            BoxSpec("waist", -3.0, -3.0, 0.5, 6, 6, 6, style="fur", base="222c52", accent="9468f0", motif="flank_rune"),
            BoxSpec("ruff", -4.0, -2.5, -6.0, 8, 5, 3, style="fur_glow", base="3d4e88", accent="c5e4ff", grow=0.06),
        ],
        children=[
            PartSpec("neck", (0.0, -1.5, -5.0), (0.38, 0.0, 0.0),
                     boxes=[BoxSpec("neck_fur", -2.5, -2.5, -2.0, 5, 5, 4, style="fur", base="2c3a69", accent="a77dff")]),
            PartSpec("left_wing_fin", (3.0, -2.5, -2.0), (-0.42, 0.32, 0.38),
                     boxes=[
                         BoxSpec("wing_base", 0.0, -4.0, -0.5, 1, 4, 5, style="crystal", base="5d6ec7", accent="9ef4ff"),
                         BoxSpec("wing_tip", 0.0, -6.5, 1.5, 1, 3, 4, style="crystal", base="7e8ff0", accent="ebffff"),
                     ]),
            PartSpec("right_wing_fin", (-3.0, -2.5, -2.0), (-0.42, -0.32, -0.38),
                     boxes=[
                         BoxSpec("wing_base", -1.0, -4.0, -0.5, 1, 4, 5, style="crystal", base="5d6ec7", accent="9ef4ff"),
                         BoxSpec("wing_tip", -1.0, -6.5, 1.5, 1, 3, 4, style="crystal", base="7e8ff0", accent="ebffff"),
                     ]),
        ],
    )

    head = PartSpec(
        "head",
        (0.0, 11.5, -6.0),
        (0.06, 0.0, 0.0),
        boxes=[
            BoxSpec("skull", -3.5, -3.0, -3.5, 7, 6, 5, style="fur", base="2b3868", accent="b794ff", motif="fox_face"),
            BoxSpec("cheek_l", 2.5, -0.5, -2.5, 2, 3, 3, style="fur_glow", base="3b4c85", accent="b8deff"),
            BoxSpec("cheek_r", -4.5, -0.5, -2.5, 2, 3, 3, style="fur_glow", base="3b4c85", accent="b8deff"),
        ],
        children=[
            PartSpec("muzzle", (0.0, 0.5, -3.5), (0.08, 0.0, 0.0),
                     boxes=[
                         BoxSpec("bridge", -2.0, -1.0, -2.0, 4, 2, 2, style="fur", base="394980", accent="a77dff"),
                         BoxSpec("snout_tip", -1.0, -0.5, -4.0, 2, 2, 2, style="fur", base="4a5c99", accent="d6eaff", motif="muzzle_nose"),
                     ]),
            PartSpec("jaw", (0.0, 2.0, -3.0), (0.06, 0.0, 0.0),
                     boxes=[BoxSpec("lower_jaw", -1.5, 0.0, -3.0, 3, 1, 3, style="fur", base="243059", accent="9468f0")]),
            PartSpec("left_ear_fin", (2.0, -2.5, -1.0), (-0.20, -0.22, 0.44),
                     boxes=[
                         BoxSpec("ear_outer", -1.0, -5.0, -0.5, 3, 5, 1, style="fur_glow", base="34457d", accent="8af0ff"),
                         BoxSpec("ear_tuft", -0.5, -7.0, -0.5, 2, 2, 1, style="crystal", base="7868e6", accent="f0ffff"),
                     ]),
            PartSpec("right_ear_fin", (-2.0, -2.5, -1.0), (-0.20, 0.22, -0.44),
                     boxes=[
                         BoxSpec("ear_outer", -2.0, -5.0, -0.5, 3, 5, 1, style="fur_glow", base="34457d", accent="8af0ff"),
                         BoxSpec("ear_tuft", -1.5, -7.0, -0.5, 2, 2, 1, style="crystal", base="7868e6", accent="f0ffff"),
                     ]),
            PartSpec("forehead_gem", (0.0, -2.5, -3.5), (-0.30, 0.0, 0.0),
                     boxes=[BoxSpec("gem", -0.5, -2.0, -0.5, 1, 2, 1, style="crystal", base="7b68ee", accent="ffffff")]),
        ],
    )

    def fox_leg(name: str, x: float, z: float, is_hind: bool) -> PartSpec:
        thigh_rx = 0.22 if is_hind else -0.14
        shin_rx = -0.42 if is_hind else 0.26
        paw_rx = 0.20 if is_hind else -0.12
        return PartSpec(
            name,
            (x, 15.5, z),
            (thigh_rx, 0.0, 0.0),
            boxes=[BoxSpec("thigh", -1.0, -0.5, -1.5, 2, 5, 3, style="fur", base="25315c", accent="9468f0")],
            children=[
                PartSpec(
                    "shin",
                    (0.0, 4.0, 0.0),
                    (shin_rx, 0.0, 0.0),
                    boxes=[BoxSpec("ankle", -1.0, 0.0, -1.0, 2, 4, 2, style="fur_glow", base="2d3c70", accent="7ce8ff")],
                    children=[
                        PartSpec("paw", (0.0, 3.5, 0.0), (paw_rx, 0.0, 0.0),
                                 boxes=[BoxSpec("foot", -1.0, 0.0, -2.0, 2, 1, 3, style="crystal", base="5d6ec7", accent="d9fbff")]),
                    ],
                )
            ],
        )

    # 3 lush fan-spread celestial tails: center_tail, left_tail, right_tail + outer plumes (total 5-plume fan silhouette!)
    def tail_branch(name: str, x: float, rx: float, ry: float, rz: float, mid_rx: float, mid_ry: float) -> PartSpec:
        return PartSpec(
            name,
            (x, 13.5, 6.0),
            (rx, ry, rz),
            boxes=[BoxSpec("base", -1.5, -1.5, 0.0, 3, 3, 6, style="fur", base="34437a", accent="a77dff")],
            children=[
                PartSpec(
                    "mid",
                    (0.0, 0.0, 5.5),
                    (mid_rx, mid_ry, 0.0),
                    boxes=[BoxSpec("plume", -2.0, -2.0, 0.0, 4, 4, 6, style="fur_glow", base="4a599c", accent="b998ff")],
                    children=[
                        PartSpec("tip", (0.0, 0.0, 5.5), (0.18, mid_ry * 0.6, 0.0),
                                 boxes=[BoxSpec("star_tip", -1.5, -1.5, 0.0, 3, 3, 5, style="crystal", base="7b68ee", accent="9ef8ff")]),
                    ],
                )
            ],
        )

    center_tail = tail_branch("center_tail", 0.0, 0.42, 0.0, 0.0, 0.24, 0.0)
    left_tail = tail_branch("left_tail", 1.5, 0.28, 0.42, 0.14, 0.20, 0.18)
    right_tail = tail_branch("right_tail", -1.5, 0.28, -0.42, -0.14, 0.20, -0.18)
    outer_left = PartSpec("outer_left_tail", (2.0, 14.0, 5.5), (0.14, 0.78, 0.26),
                          boxes=[BoxSpec("plume", -1.0, -1.5, 0.0, 2, 3, 7, style="fur_glow", base="455494", accent="85f0ff")])
    outer_right = PartSpec("outer_right_tail", (-2.0, 14.0, 5.5), (0.14, -0.78, -0.26),
                           boxes=[BoxSpec("plume", -1.0, -1.5, 0.0, 2, 3, 7, style="fur_glow", base="455494", accent="85f0ff")])

    return [
        body,
        head,
        fox_leg("front_right_leg", -2.2, -3.8, False),
        fox_leg("front_left_leg", 2.2, -3.8, False),
        fox_leg("back_right_leg", -2.2, 4.2, True),
        fox_leg("back_left_leg", 2.2, 4.2, True),
        center_tail,
        left_tail,
        right_tail,
        outer_left,
        outer_right,
    ]


# ---------------------------------------------------------------------------
# 3. Rune Wolf (rune_wolf, 128x128)
# ---------------------------------------------------------------------------
def spec_rune_wolf() -> list[PartSpec]:
    armor = PartSpec(
        "armor",
        (0.0, 0.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[
            BoxSpec("saddle_plate", -4.5, -4.5, -5.0, 9, 4, 9, style="metal", base="2f3440", accent="d4a64a", grow=0.10, motif="flank_rune"),
        ],
        children=[
            PartSpec("pauldron_left", (3.6, -2.2, -3.5), (0.08, 0.14, 0.34),
                     boxes=[
                         BoxSpec("plate", -1.0, -1.0, -3.0, 2, 5, 6, style="metal", base="363c4a", accent="d4a64a"),
                         BoxSpec("rune_crista", 0.2, -2.5, -2.0, 1, 2, 4, style="crystal", base="1d8fa3", accent="8affff"),
                     ]),
            PartSpec("pauldron_right", (-3.6, -2.2, -3.5), (0.08, -0.14, -0.34),
                     boxes=[
                         BoxSpec("plate", -1.0, -1.0, -3.0, 2, 5, 6, style="metal", base="363c4a", accent="d4a64a"),
                         BoxSpec("rune_crista", -1.2, -2.5, -2.0, 1, 2, 4, style="crystal", base="1d8fa3", accent="8affff"),
                     ]),
            PartSpec("back_rune", (0.0, -4.8, -0.5), (-0.18, 0.0, 0.0),
                     boxes=[BoxSpec("dorsal_prism", -1.5, -1.5, -2.0, 3, 2, 5, style="crystal", base="1c7d94", accent="7af8ff")]),
        ],
    )

    mane = PartSpec(
        "mane",
        (0.0, -2.0, -5.0),
        (0.18, 0.0, 0.0),
        boxes=[BoxSpec("ruff_core", -4.5, -3.5, -3.0, 9, 7, 5, style="fur", base="262b36", accent="38e5f5", grow=0.05)],
        children=[
            PartSpec("mane_crest_top", (0.0, -3.5, -1.5), (0.28, 0.0, 0.0),
                     boxes=[BoxSpec("spikes", -2.0, -1.5, 0.0, 4, 2, 6, style="fur_glow", base="2c3340", accent="4cf2ff")]),
            PartSpec("mane_crest_left", (3.8, -1.5, -1.0), (0.18, 0.34, 0.24),
                     boxes=[BoxSpec("tuft", -1.0, -1.5, 0.0, 2, 3, 6, style="fur_glow", base="2a303d", accent="38e5f5")]),
            PartSpec("mane_crest_right", (-3.8, -1.5, -1.0), (0.18, -0.34, -0.24),
                     boxes=[BoxSpec("tuft", -1.0, -1.5, 0.0, 2, 3, 6, style="fur_glow", base="2a303d", accent="38e5f5")]),
        ],
    )

    body = PartSpec(
        "body",
        (0.0, 13.5, 0.0),
        (-0.06, 0.0, 0.0),
        boxes=[
            BoxSpec("ribcage", -4.0, -4.0, -6.0, 8, 8, 7, style="fur", base="232730", accent="38e5f5", motif="flank_rune"),
            BoxSpec("loin", -3.0, -3.2, 1.0, 6, 6, 6, style="fur", base="1e222b", accent="38e5f5"),
        ],
        children=[armor, mane],
    )

    head = PartSpec(
        "head",
        (0.0, 10.5, -7.5),
        (0.05, 0.0, 0.0),
        boxes=[
            BoxSpec("skull", -3.5, -3.5, -4.0, 7, 7, 6, style="fur", base="2b303b", accent="4af2ff", motif="wolf_face"),
            BoxSpec("brow_plate", -3.5, -3.8, -4.2, 7, 2, 4, style="metal", base="3a404f", accent="d4a64a", grow=0.06),
        ],
        children=[
            PartSpec("snout", (0.0, 0.5, -4.0), (0.06, 0.0, 0.0),
                     boxes=[BoxSpec("muzzle", -2.0, -1.0, -4.0, 4, 3, 4, style="fur", base="383e4c", accent="d4a64a", motif="muzzle_nose")]),
            PartSpec("jaw", (0.0, 2.2, -4.0), (0.08, 0.0, 0.0),
                     boxes=[BoxSpec("mandible", -1.5, 0.0, -3.5, 3, 1, 4, style="fur", base="222630", accent="38e5f5")]),
            PartSpec("lens", (0.0, -2.5, -4.1), (-0.06, 0.0, 0.0),
                     boxes=[BoxSpec("rune_gem", -0.5, -1.0, -0.2, 1, 2, 1, style="crystal", base="1aa3b8", accent="9effff")]),
            PartSpec("left_ear", (2.4, -3.0, -1.0), (-0.26, -0.18, 0.34),
                     boxes=[
                         BoxSpec("pinna", -1.0, -4.5, -1.0, 2, 5, 2, style="fur_glow", base="2c313d", accent="4ef4ff"),
                     ]),
            PartSpec("right_ear", (-2.4, -3.0, -1.0), (-0.26, 0.18, -0.34),
                     boxes=[
                         BoxSpec("pinna", -1.0, -4.5, -1.0, 2, 5, 2, style="fur_glow", base="2c313d", accent="4ef4ff"),
                     ]),
        ],
    )

    def wolf_leg(name: str, x: float, z: float, is_hind: bool) -> PartSpec:
        thigh_rx = 0.34 if is_hind else -0.18
        shin_rx = -0.62 if is_hind else 0.34
        paw_rx = 0.28 if is_hind else -0.16
        return PartSpec(
            name,
            (x, 14.5, z),
            (thigh_rx, 0.0, 0.0),
            boxes=[
                BoxSpec("thigh", -1.5, -0.5, -1.5, 3, 5, 3, style="fur", base="252a34", accent="38e5f5"),
                BoxSpec("greave", -1.5, 0.5, -1.5, 3, 3, 3, style="metal", base="383e4d", accent="d4a64a", grow=0.08),
            ],
            children=[
                PartSpec(
                    "shin",
                    (0.0, 4.2, 0.2),
                    (shin_rx, 0.0, 0.0),
                    boxes=[BoxSpec("metatarsus", -1.0, 0.0, -1.0, 2, 5, 2, style="fur", base="2d3340", accent="38e5f5")],
                    children=[
                        PartSpec("paw", (0.0, 4.5, -0.2), (paw_rx, 0.0, 0.0),
                                 boxes=[BoxSpec("claws", -1.5, 0.0, -2.5, 3, 2, 4, style="metal", base="3d4250", accent="5cf5ff")]),
                    ],
                )
            ],
        )

    # Wolf tail: sweeps slightly downward from croup then curves gracefully upward at tip
    tail = PartSpec(
        "tail",
        (0.0, 12.5, 6.5),
        (-0.32, 0.0, 0.0),
        boxes=[BoxSpec("tail_base", -1.5, -1.5, 0.0, 3, 3, 6, style="fur", base="252a34", accent="38e5f5")],
        children=[
            PartSpec(
                "tail_mid",
                (0.0, 0.0, 5.5),
                (0.38, 0.0, 0.0),
                boxes=[BoxSpec("tail_plume", -2.0, -2.0, 0.0, 4, 4, 6, style="fur_glow", base="2f3644", accent="38e5f5")],
                children=[
                    PartSpec("tail_tip", (0.0, 0.0, 5.5), (0.36, 0.0, 0.0),
                             boxes=[BoxSpec("tail_blade", -1.5, -1.5, 0.0, 3, 3, 5, style="crystal", base="1d8da3", accent="9affff")]),
                ],
            )
        ],
    )

    return [
        body,
        head,
        wolf_leg("front_right_leg", -2.6, -4.5, False),
        wolf_leg("front_left_leg", 2.6, -4.5, False),
        wolf_leg("back_right_leg", -2.5, 4.8, True),
        wolf_leg("back_left_leg", 2.5, 4.8, True),
        tail,
    ]


# ---------------------------------------------------------------------------
# 4. Runic Watcher (runic_watcher, 128x128) — Floating Arcane Sentinel Golem
# ---------------------------------------------------------------------------
def spec_runic_watcher() -> list[PartSpec]:
    head = PartSpec(
        "head",
        (0.0, 4.5, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[
            BoxSpec("helm", -3.5, -7.0, -3.5, 7, 7, 7, style="stone_rune", base="363846", accent="56ecfa"),
            BoxSpec("brow_trim", -4.0, -7.0, -4.0, 8, 3, 8, style="metal", base="9c7b42", accent="f5d67b", grow=0.08),
        ],
        children=[
            PartSpec("halo", (0.0, -8.8, 0.0), (0.26, 0.0, -0.22),
                     boxes=[
                         BoxSpec("ring_n", -5.0, -0.5, -5.0, 10, 1, 2, style="metal", base="b58f4c", accent="7af8ff"),
                         BoxSpec("ring_s", -5.0, -0.5, 3.0, 10, 1, 2, style="metal", base="b58f4c", accent="7af8ff"),
                         BoxSpec("ring_e", -5.0, -0.5, -3.0, 2, 1, 6, style="metal", base="b58f4c", accent="7af8ff"),
                         BoxSpec("ring_w", 3.0, -0.5, -3.0, 2, 1, 6, style="metal", base="b58f4c", accent="7af8ff"),
                         BoxSpec("crown_spire", -1.0, -3.5, -5.0, 2, 3, 1, style="crystal", base="2ba8c4", accent="d6ffff"),
                     ]),
            PartSpec("lens", (0.0, -3.5, -3.8), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("eye_prism", -2.0, -1.5, -0.5, 4, 3, 1, style="crystal", base="1d9db8", accent="c8ffff", motif="core_sigil")]),
            PartSpec("crest_left", (3.8, -5.5, -1.0), (-0.22, 0.14, 0.42),
                     boxes=[BoxSpec("horn", 0.0, -4.0, -1.0, 1, 5, 3, style="metal", base="a88546", accent="ffe18c")]),
            PartSpec("crest_right", (-3.8, -5.5, -1.0), (-0.22, -0.14, -0.42),
                     boxes=[BoxSpec("horn", -1.0, -4.0, -1.0, 1, 5, 3, style="metal", base="a88546", accent="ffe18c")]),
        ],
    )

    torso = PartSpec(
        "torso",
        (0.0, 5.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[
            BoxSpec("cuirass", -5.5, 0.0, -3.0, 11, 7, 6, style="stone_rune", base="303342", accent="4ee8f8", motif="flank_rune"),
            BoxSpec("rib_frame", -5.0, 0.5, -3.5, 10, 4, 7, style="metal", base="8f6f3b", accent="f0ce73", grow=0.06),
            BoxSpec("waist_core", -3.0, 7.0, -2.0, 6, 4, 4, style="crystal", base="237e9c", accent="9efaff"),
        ],
        children=[
            PartSpec("chest_rune", (0.0, 4.0, -3.2), (0.0, 0.0, 0.7854),
                     boxes=[
                         BoxSpec("outer_frame", -2.5, -2.5, -0.5, 5, 5, 1, style="metal", base="b08b49", accent="ffe082"),
                         BoxSpec("inner_heart", -1.5, -1.5, -1.0, 3, 3, 2, style="crystal", base="24b6d4", accent="ffffff", motif="core_sigil"),
                     ]),
            PartSpec("shoulder_wing_l", (5.5, 0.5, 0.0), (0.08, -0.12, -0.34),
                     boxes=[
                         BoxSpec("pauldron_arch", 0.0, -2.5, -3.5, 6, 3, 7, style="metal", base="9e7c43", accent="f7d87c"),
                         BoxSpec("wing_flange", 4.0, 0.5, -2.5, 2, 6, 5, style="stone_rune", base="393c4d", accent="56ecfa"),
                     ]),
            PartSpec("shoulder_wing_r", (-5.5, 0.5, 0.0), (0.08, 0.12, 0.34),
                     boxes=[
                         BoxSpec("pauldron_arch", -6.0, -2.5, -3.5, 6, 3, 7, style="metal", base="9e7c43", accent="f7d87c"),
                         BoxSpec("wing_flange", -6.0, 0.5, -2.5, 2, 6, 5, style="stone_rune", base="393c4d", accent="56ecfa"),
                     ]),
        ],
    )

    def watcher_arm(name: str, side: float) -> PartSpec:
        # side: -1 right, +1 left
        sx = side * 7.5
        roll = side * -0.14
        return PartSpec(
            name,
            (sx, 6.5, 0.0),
            (-0.12, 0.0, roll),
            boxes=[
                BoxSpec("upper_stone", -1.5, 0.0, -1.5, 3, 5, 3, style="stone_rune", base="343747", accent="56ecfa"),
                BoxSpec("energy_joint", -1.0, 4.5, -1.0, 2, 2, 2, style="crystal", base="25a8c4", accent="b8ffff"),
            ],
            children=[
                PartSpec(
                    "forearm",
                    (0.0, 6.0, 0.0),
                    (-0.26, 0.0, -roll * 0.6),
                    boxes=[
                        BoxSpec("gauntlet", -2.0, 0.0, -2.0, 4, 6, 4, style="stone_rune", base="2c2f3d", accent="56ecfa"),
                        BoxSpec("cuff_trim", -2.0, 0.0, -2.0, 4, 2, 4, style="metal", base="9c7b42", accent="f5d67b", grow=0.10),
                    ],
                    children=[
                        PartSpec("claw_inner", (-side * 1.2, 5.8, -0.5), (0.18, 0.0, side * 0.28),
                                 boxes=[BoxSpec("prong", -0.5, 0.0, -1.0, 1, 3, 2, style="crystal", base="24b6d4", accent="e6ffff")]),
                        PartSpec("claw_outer", (side * 1.2, 5.8, -0.5), (0.18, 0.0, -side * 0.24),
                                 boxes=[BoxSpec("prong", -0.5, 0.0, -1.0, 1, 4, 2, style="crystal", base="24b6d4", accent="e6ffff")]),
                    ],
                )
            ],
        )

    # Lower body: hovering ring of tilted arcane monolith pillars instead of blocky pants!
    right_leg = PartSpec(
        "right_leg",
        (-3.2, 14.5, -1.5),
        (-0.16, 0.22, 0.20),
        boxes=[
            BoxSpec("pillar_front", -1.5, 0.0, -1.5, 3, 8, 3, style="stone_rune", base="323545", accent="56ecfa"),
            BoxSpec("cap_gold", -1.5, 0.0, -1.5, 3, 2, 3, style="metal", base="9c7b42", accent="f5d67b", grow=0.08),
        ],
        children=[
            PartSpec("rear_shard", (0.5, 0.5, 3.5), (0.32, -0.25, 0.14),
                     boxes=[BoxSpec("shard", -1.0, 0.0, -1.0, 2, 7, 2, style="crystal", base="238ca8", accent="a8ffff")]),
        ],
    )
    left_leg = PartSpec(
        "left_leg",
        (3.2, 14.5, -1.5),
        (-0.16, -0.22, -0.20),
        boxes=[
            BoxSpec("pillar_front", -1.5, 0.0, -1.5, 3, 8, 3, style="stone_rune", base="323545", accent="56ecfa"),
            BoxSpec("cap_gold", -1.5, 0.0, -1.5, 3, 2, 3, style="metal", base="9c7b42", accent="f5d67b", grow=0.08),
        ],
        children=[
            PartSpec("rear_shard", (-0.5, 0.5, 3.5), (0.32, 0.25, -0.14),
                     boxes=[BoxSpec("shard", -1.0, 0.0, -1.0, 2, 7, 2, style="crystal", base="238ca8", accent="a8ffff")]),
        ],
    )

    right_pylon = PartSpec(
        "right_pylon",
        (-5.2, 5.5, 3.8),
        (-0.28, -0.18, -0.38),
        boxes=[BoxSpec("obelisk", -1.0, -7.0, -1.0, 2, 9, 2, style="stone_rune", base="383c4f", accent="56ecfa")],
        children=[
            PartSpec("tip", (0.0, -7.0, 0.0), (0.0, 0.7854, 0.0),
                     boxes=[BoxSpec("apex", -1.0, -3.0, -1.0, 2, 3, 2, style="crystal", base="24b6d4", accent="e6ffff")]),
        ],
    )
    left_pylon = PartSpec(
        "left_pylon",
        (5.2, 5.5, 3.8),
        (-0.28, 0.18, 0.38),
        boxes=[BoxSpec("obelisk", -1.0, -7.0, -1.0, 2, 9, 2, style="stone_rune", base="383c4f", accent="56ecfa")],
        children=[
            PartSpec("tip", (0.0, -7.0, 0.0), (0.0, 0.7854, 0.0),
                     boxes=[BoxSpec("apex", -1.0, -3.0, -1.0, 2, 3, 2, style="crystal", base="24b6d4", accent="e6ffff")]),
        ],
    )

    return [head, torso, watcher_arm("right_arm", -1.0), watcher_arm("left_arm", 1.0), right_leg, left_leg, right_pylon, left_pylon]


# ---------------------------------------------------------------------------
# 5. Mana Wisp / Bioluminescent Spore-Jelly Sprite (mana_wisp, 128x128)
#    Features crystalline translucent jelly core + outer gel membrane +
#    multi-tiered flared arcane mushroom/umbrella cap + tilted fruiting buds +
#    hanging glowing spore-tendrils + orbiting motes!
# ---------------------------------------------------------------------------
def spec_mana_wisp() -> list[PartSpec]:
    crown = PartSpec(
        "crown",
        (0.0, -3.5, 0.0),
        (-0.08, 0.0, 0.04),
        boxes=[
            BoxSpec("cap_dome", -4.5, -3.0, -4.5, 9, 3, 9, style="fungal_cap", base="5e36a8", accent="58eefc", alpha=240),
            BoxSpec("cap_apex", -3.0, -5.0, -3.0, 6, 2, 6, style="fungal_cap", base="7647c9", accent="9efaff", alpha=245),
        ],
        children=[
            PartSpec("brim_front", (0.0, -0.5, -4.2), (0.34, 0.0, 0.0),
                     boxes=[BoxSpec("eave", -5.0, 0.0, -2.0, 10, 1, 3, style="fungal_cap", base="4c2c8f", accent="6cf4ff", alpha=230)]),
            PartSpec("brim_back", (0.0, -0.5, 4.2), (-0.34, 0.0, 0.0),
                     boxes=[BoxSpec("eave", -5.0, 0.0, -1.0, 10, 1, 3, style="fungal_cap", base="4c2c8f", accent="6cf4ff", alpha=230)]),
            PartSpec("brim_left", (4.2, -0.5, 0.0), (0.0, 0.0, -0.34),
                     boxes=[BoxSpec("eave", -1.0, 0.0, -4.0, 3, 1, 8, style="fungal_cap", base="4c2c8f", accent="6cf4ff", alpha=230)]),
            PartSpec("brim_right", (-4.2, -0.5, 0.0), (0.0, 0.0, 0.34),
                     boxes=[BoxSpec("eave", -2.0, 0.0, -4.0, 3, 1, 8, style="fungal_cap", base="4c2c8f", accent="6cf4ff", alpha=230)]),
            # Tilted glowing mini mushroom fruiting bodies on top of cap
            PartSpec("bud_large", (1.8, -4.8, -0.8), (0.16, 0.20, 0.36),
                     boxes=[
                         BoxSpec("stem", -0.5, -2.0, -0.5, 1, 2, 1, style="gel", base="9af6ff", accent="ffffff"),
                         BoxSpec("mini_cap", -1.5, -3.5, -1.5, 3, 2, 3, style="crystal", base="8a56eb", accent="8affff"),
                     ]),
            PartSpec("bud_small", (-2.0, -4.4, 1.2), (-0.22, -0.15, -0.42),
                     boxes=[
                         BoxSpec("stem", -0.5, -2.0, -0.5, 1, 2, 1, style="gel", base="9af6ff", accent="ffffff"),
                         BoxSpec("mini_cap", -1.0, -3.0, -1.0, 2, 1, 2, style="crystal", base="a86cf5", accent="b8ffff"),
                     ]),
        ],
    )

    core = PartSpec(
        "core",
        (0.0, 12.5, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[
            BoxSpec("inner_nucleus", -2.5, -2.5, -2.5, 5, 5, 5, style="crystal", base="6f45d6", accent="b5ffff", motif="core_sigil"),
            BoxSpec("outer_gel", -4.0, -3.5, -4.0, 8, 7, 8, style="gel", base="3b7ea8", accent="8af8ff", grow=0.12, alpha=172),
        ],
        children=[crown],
    )

    ring = PartSpec(
        "ring",
        (0.0, 14.5, 0.0),
        (0.24, 0.0, -0.20),
        boxes=[
            BoxSpec("arc_n", -5.5, -0.5, -5.5, 11, 1, 1, style="metal", base="b58f48", accent="ffe694"),
            BoxSpec("arc_s", -5.5, -0.5, 4.5, 11, 1, 1, style="metal", base="b58f48", accent="ffe694"),
            BoxSpec("arc_e", -5.5, -0.5, -4.5, 1, 1, 9, style="metal", base="b58f48", accent="ffe694"),
            BoxSpec("arc_w", 4.5, -0.5, -4.5, 1, 1, 9, style="metal", base="b58f48", accent="ffe694"),
        ],
    )

    inner_ring = PartSpec(
        "inner_ring",
        (0.0, 12.5, 0.0),
        (-0.34, 0.35, 0.28),
        boxes=[
            BoxSpec("shard_l", 4.5, -1.5, -1.0, 1, 3, 2, style="crystal", base="56d8f0", accent="ebffff"),
            BoxSpec("shard_r", -5.5, -1.5, -1.0, 1, 3, 2, style="crystal", base="56d8f0", accent="ebffff"),
        ],
    )

    left_fin = PartSpec(
        "left_fin",
        (3.8, 12.0, 0.0),
        (0.12, -0.28, -0.32),
        boxes=[
            BoxSpec("wing_upper", 0.0, -3.0, -0.5, 5, 4, 1, style="crystal", base="4ecae6", accent="e6ffff", alpha=215),
            BoxSpec("wing_lower", 0.5, 1.0, -0.5, 3, 3, 1, style="gel", base="764ce0", accent="8af8ff", alpha=205),
        ],
    )
    right_fin = PartSpec(
        "right_fin",
        (-3.8, 12.0, 0.0),
        (0.12, 0.28, 0.32),
        boxes=[
            BoxSpec("wing_upper", -5.0, -3.0, -0.5, 5, 4, 1, style="crystal", base="4ecae6", accent="e6ffff", alpha=215),
            BoxSpec("wing_lower", -3.5, 1.0, -0.5, 3, 3, 1, style="gel", base="764ce0", accent="8af8ff", alpha=205),
        ],
    )

    # Hanging glowing spore filaments + central tail tendril
    flame = PartSpec(
        "flame",
        (0.0, 15.5, 0.0),
        (0.08, 0.0, 0.0),
        boxes=[BoxSpec("central_stalk", -1.5, 0.0, -1.5, 3, 4, 3, style="gel", base="5d3ec4", accent="7ef6ff", alpha=215)],
        children=[
            PartSpec("flame_tip", (0.0, 3.8, 0.0), (0.14, 0.0, 0.0),
                     boxes=[BoxSpec("wisp_tail", -1.0, 0.0, -1.0, 2, 4, 2, style="crystal", base="4ad8f0", accent="ebffff", alpha=220)]),
            PartSpec("filament_fl", (2.4, -0.5, -2.0), (0.16, 0.0, -0.18),
                     boxes=[BoxSpec("silk", -0.5, 0.0, -0.5, 1, 6, 1, style="crystal", base="7a52e0", accent="9efcff", alpha=215)]),
            PartSpec("filament_fr", (-2.4, -0.5, -2.0), (0.16, 0.0, 0.18),
                     boxes=[BoxSpec("silk", -0.5, 0.0, -0.5, 1, 6, 1, style="crystal", base="7a52e0", accent="9efcff", alpha=215)]),
            PartSpec("filament_bl", (2.2, -0.5, 2.2), (-0.18, 0.0, -0.16),
                     boxes=[BoxSpec("silk", -0.5, 0.0, -0.5, 1, 5, 1, style="crystal", base="56cfe8", accent="ebffff", alpha=215)]),
            PartSpec("filament_br", (-2.2, -0.5, 2.2), (-0.18, 0.0, 0.16),
                     boxes=[BoxSpec("silk", -0.5, 0.0, -0.5, 1, 5, 1, style="crystal", base="56cfe8", accent="ebffff", alpha=215)]),
        ],
    )

    motes = []
    mote_positions = [(5.5, 11.0, -2.0), (-5.2, 13.5, 2.2), (0.0, 9.5, 5.2)]
    for idx, ch in enumerate(("a", "b", "c")):
        mx, my, mz = mote_positions[idx]
        motes.append(
            PartSpec(
                f"mote_{ch}",
                (mx, my, mz),
                (0.35, 0.45, 0.25),
                boxes=[BoxSpec("spore", -0.5, -0.5, -0.5, 1, 1, 1, style="crystal", base="8affff", accent="ffffff")],
                children=[
                    PartSpec("glow", (0.0, 0.0, 0.0), (0.0, 0.0, 0.0),
                             boxes=[BoxSpec("halo", -1.0, -1.0, -1.0, 2, 2, 2, style="gel", base="6b48d6", accent="9efcff", grow=0.05, alpha=150)]),
                ],
            )
        )

    return [core, ring, inner_ring, left_fin, right_fin, flame] + motes


# ---------------------------------------------------------------------------
# 6. Arcane Settler (arcane_settler, 128x128)
# ---------------------------------------------------------------------------
def spec_arcane_settler(role_motif: str = "settler_face_witch", skin_hex: str = "dba88a",
                        hair_hex: str = "3b2838", coat_hex: str = "3b2a54",
                        trim_hex: str = "e0b354", pack_hex: str = "4f3829") -> list[PartSpec]:
    witch_hat = PartSpec(
        "witch_hat",
        (0.0, -8.0, 0.0),
        (-0.08, 0.0, 0.04),
        boxes=[
            BoxSpec("brim_main", -6.0, -1.0, -6.0, 12, 1, 12, style="cloth", base=coat_hex, accent=trim_hex),
            BoxSpec("hat_band", -4.5, -2.0, -4.5, 9, 1, 9, style="metal", base=trim_hex, accent="fff0a8", grow=0.05),
        ],
        children=[
            PartSpec(
                "crown",
                (0.0, -2.0, 0.0),
                (-0.18, 0.0, 0.08),
                boxes=[BoxSpec("cone_mid", -3.5, -4.0, -3.5, 7, 4, 7, style="cloth", base=coat_hex, accent=trim_hex)],
                children=[
                    PartSpec(
                        "tip",
                        (0.0, -3.8, 0.0),
                        (-0.38, 0.0, 0.22),
                        boxes=[
                            BoxSpec("cone_upper", -2.0, -4.0, -2.0, 4, 4, 4, style="cloth", base=coat_hex, accent=trim_hex),
                            BoxSpec("star_charm", -0.5, -5.0, -1.5, 1, 2, 1, style="crystal", base="56e2f5", accent="ffffff"),
                        ],
                    )
                ],
            )
        ],
    )

    head = PartSpec(
        "head",
        (0.0, 0.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[BoxSpec("cranium", -4.0, -8.0, -4.0, 8, 8, 8, style="skin", base=skin_hex, accent=trim_hex, motif=role_motif)],
        children=[
            PartSpec("hair_cap", (0.0, 0.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[
                         BoxSpec("bangs", -4.5, -8.5, -4.5, 9, 3, 9, style="hair", base=hair_hex, accent=trim_hex, grow=0.05),
                         BoxSpec("back_locks", -4.0, -6.0, 3.2, 8, 7, 2, style="hair", base=hair_hex, accent=trim_hex),
                     ]),
            witch_hat,
            PartSpec("goblin_ears", (0.0, -4.5, -0.5), (0.0, 0.0, 0.0),
                     children=[
                         PartSpec("ear_l", (4.0, 0.0, 0.0), (0.0, -0.22, -0.28),
                                  boxes=[BoxSpec("pinna_l", 0.0, -1.5, -0.5, 4, 2, 1, style="skin", base=skin_hex, accent=trim_hex)]),
                         PartSpec("ear_r", (-4.0, 0.0, 0.0), (0.0, 0.22, 0.28),
                                  boxes=[BoxSpec("pinna_r", -4.0, -1.5, -0.5, 4, 2, 1, style="skin", base=skin_hex, accent=trim_hex)]),
                     ]),
            PartSpec("lens", (2.2, -4.2, -4.2), (0.0, -0.08, 0.0),
                     boxes=[BoxSpec("monocle", -1.5, -1.5, -0.5, 3, 3, 1, style="crystal", base="2ba6bf", accent="aefaff")]),
        ],
    )

    body = PartSpec(
        "body",
        (0.0, 0.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[
            BoxSpec("torso_inner", -4.0, 0.0, -2.0, 8, 12, 4, style="cloth", base=coat_hex, accent=trim_hex),
        ],
        children=[
            PartSpec("coat", (0.0, 0.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("vestment", -4.5, -0.2, -2.5, 9, 12, 5, style="cloth", base=coat_hex, accent=trim_hex, grow=0.04, motif="robe_front")]),
            # Angled arcane mantle / shoulder cape
            PartSpec("mantle_left", (3.8, 0.5, 0.0), (0.06, 0.0, 0.26),
                     boxes=[BoxSpec("shawl_l", -0.5, -1.0, -3.0, 5, 3, 6, style="cloth", base=coat_hex, accent=trim_hex, grow=0.08)]),
            PartSpec("mantle_right", (-3.8, 0.5, 0.0), (0.06, 0.0, -0.26),
                     boxes=[BoxSpec("shawl_r", -4.5, -1.0, -3.0, 5, 3, 6, style="cloth", base=coat_hex, accent=trim_hex, grow=0.08)]),
            # Waist spellbook grimoire & glowing potion flask!
            PartSpec("grimoire", (-4.6, 9.2, -0.5), (0.14, 0.12, 0.24),
                     boxes=[
                         BoxSpec("tome_cover", -1.0, 0.0, -2.0, 2, 5, 4, style="leather", base="5e2738", accent=trim_hex),
                         BoxSpec("tome_gem", -1.2, 1.5, -0.5, 1, 2, 1, style="crystal", base="56e2f5", accent="ffffff"),
                     ]),
            PartSpec("waist_vial", (4.5, 9.2, -1.2), (-0.16, -0.14, -0.22),
                     boxes=[BoxSpec("potion", -1.0, 0.0, -1.0, 2, 4, 2, style="crystal", base="944ee8", accent="9efcff")]),
            # Split flared lower robe coat skirt
            PartSpec(
                "coat_skirt",
                (0.0, 10.5, 0.0),
                (0.04, 0.0, 0.0),
                boxes=[
                    BoxSpec("front_apron", -3.5, 0.0, -2.6, 7, 8, 1, style="cloth", base=coat_hex, accent=trim_hex),
                    BoxSpec("back_tail", -4.5, 0.0, 1.6, 9, 9, 1, style="cloth", base=coat_hex, accent=trim_hex),
                ],
                children=[
                    PartSpec("skirt_left", (4.2, 0.0, 0.0), (0.0, 0.0, -0.18),
                             boxes=[BoxSpec("panel_l", -1.0, 0.0, -2.5, 2, 9, 5, style="cloth", base=coat_hex, accent=trim_hex)]),
                    PartSpec("skirt_right", (-4.2, 0.0, 0.0), (0.0, 0.0, 0.18),
                             boxes=[BoxSpec("panel_r", -1.0, 0.0, -2.5, 2, 9, 5, style="cloth", base=coat_hex, accent=trim_hex)]),
                ],
            ),
        ],
    )

    right_arm = PartSpec(
        "right_arm",
        (-5.0, 2.0, 0.0),
        (-0.06, 0.0, 0.08),
        boxes=[BoxSpec("arm_r", -3.0, -2.0, -2.0, 4, 12, 4, style="skin", base=skin_hex, accent=trim_hex)],
        children=[
            PartSpec("right_sleeve", (0.0, 0.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("upper_sleeve", -3.5, -2.0, -2.5, 5, 7, 5, style="cloth", base=coat_hex, accent=trim_hex, grow=0.04)]),
            PartSpec("right_cuff", (-1.5, 4.8, 0.2), (0.14, 0.0, 0.12),
                     boxes=[BoxSpec("drape_cuff", -2.5, 0.0, -3.0, 5, 5, 6, style="cloth", base=coat_hex, accent=trim_hex)]),
        ],
    )
    left_arm = PartSpec(
        "left_arm",
        (5.0, 2.0, 0.0),
        (-0.06, 0.0, -0.08),
        boxes=[BoxSpec("arm_l", -1.0, -2.0, -2.0, 4, 12, 4, style="skin", base=skin_hex, accent=trim_hex)],
        children=[
            PartSpec("left_sleeve", (0.0, 0.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("upper_sleeve", -1.5, -2.0, -2.5, 5, 7, 5, style="cloth", base=coat_hex, accent=trim_hex, grow=0.04)]),
            PartSpec("left_cuff", (1.5, 4.8, 0.2), (0.14, 0.0, -0.12),
                     boxes=[BoxSpec("drape_cuff", -2.5, 0.0, -3.0, 5, 5, 6, style="cloth", base=coat_hex, accent=trim_hex)]),
        ],
    )

    right_leg = PartSpec(
        "right_leg",
        (-2.0, 12.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[BoxSpec("trouser_r", -2.0, 0.0, -2.0, 4, 12, 4, style="leather", base="2d2538", accent=trim_hex)],
        children=[
            PartSpec("right_boot", (0.0, 7.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("boot_r", -2.5, 0.0, -2.8, 5, 5, 5, style="leather", base="3b2820", accent=trim_hex)]),
        ],
    )
    left_leg = PartSpec(
        "left_leg",
        (2.0, 12.0, 0.0),
        (0.0, 0.0, 0.0),
        boxes=[BoxSpec("trouser_l", -2.0, 0.0, -2.0, 4, 12, 4, style="leather", base="2d2538", accent=trim_hex)],
        children=[
            PartSpec("left_boot", (0.0, 7.0, 0.0), (0.0, 0.0, 0.0),
                     boxes=[BoxSpec("boot_l", -2.5, 0.0, -2.8, 5, 5, 5, style="leather", base="3b2820", accent=trim_hex)]),
        ],
    )

    witch_pack = PartSpec(
        "witch_pack",
        (0.0, 2.0, 2.5),
        (0.06, 0.0, 0.0),
        boxes=[BoxSpec("satchel", -4.0, 0.0, 0.0, 8, 10, 4, style="leather", base=pack_hex, accent=trim_hex)],
        children=[
            PartSpec("herb_bottle", (4.2, 4.5, 2.0), (0.0, 0.0, -0.22),
                     boxes=[BoxSpec("flask", -1.5, -2.0, -1.5, 3, 5, 3, style="crystal", base="8f4ae3", accent="a8ffff")]),
        ],
    )
    goblin_pack = PartSpec(
        "goblin_pack",
        (0.0, 2.5, 2.5),
        (0.08, 0.0, 0.0),
        boxes=[BoxSpec("crate_pack", -4.5, 0.0, 0.0, 9, 10, 5, style="leather", base=pack_hex, accent=trim_hex)],
        children=[
            PartSpec("tool_roll", (0.0, -1.5, 2.5), (0.0, 0.0, 0.06),
                     boxes=[BoxSpec("bedroll", -5.0, -1.5, -1.5, 10, 3, 3, style="cloth", base="785334", accent=trim_hex)]),
        ],
    )
    researcher_pack = PartSpec(
        "researcher_pack",
        (0.0, 2.0, 2.5),
        (0.05, 0.0, 0.0),
        boxes=[BoxSpec("book_case", -4.0, 0.0, 0.0, 8, 11, 4, style="leather", base=pack_hex, accent=trim_hex)],
        children=[
            PartSpec("scroll_tube", (4.0, 4.0, 2.0), (0.0, 0.0, -0.26),
                     boxes=[BoxSpec("cylinder", -1.5, -6.0, -1.5, 3, 12, 3, style="metal", base="8c6a38", accent="ffe28a")]),
        ],
    )

    return [head, body, right_arm, left_arm, right_leg, left_leg, witch_pack, goblin_pack, researcher_pack]


# ---------------------------------------------------------------------------
# Item Icons (Spawn Eggs + Familiar Contract)
# ---------------------------------------------------------------------------
def egg_icon(path: Path, base: str, spots: str, rune: str) -> None:
    image = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    b = hex_rgb(base)
    s = hex_rgb(spots)
    r = hex_rgb(rune)
    for y in range(3, 29):
        ty = (y - 3) / 25.0
        half = int(10 * (1.0 - abs(y - 17) / 18.0)) + 2
        for x in range(16 - half, 17 + half):
            tx = (x - (16 - half)) / max(1, 2 * half)
            cyl = -18.0 * ((2.0 * tx - 1.0) ** 2) + (0.45 - ty) * 20.0
            draw.point((x, y), fill=rgba(b, cyl))
    for x, y in ((11, 10), (20, 13), (13, 20), (21, 23), (16, 7)):
        draw.rectangle((x, y, x + 2, y + 2), fill=rgba(s, 8))
    draw.line((12, 17, 16, 12, 20, 17, 16, 22, 12, 17), fill=rgba(r, 24), width=1)
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def contract_icon(path: Path) -> None:
    image = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    paper = hex_rgb("dfd6c3")
    brass = hex_rgb("c79742")
    violet = hex_rgb("8754d6")
    cyan = hex_rgb("48cfe0")
    draw.rounded_rectangle((4, 3, 27, 28), radius=2, fill=rgba(paper, 0), outline=rgba(brass, -22), width=2)
    draw.rectangle((7, 6, 24, 25), outline=rgba(violet, -4), width=1)
    draw.ellipse((10, 9, 21, 20), outline=rgba(brass, 8), width=2)
    draw.line((11, 15, 16, 10, 21, 15, 16, 22, 11, 15), fill=rgba(violet, 18), width=1)
    draw.line((9, 23, 23, 23), fill=rgba(cyan, 12), width=1)
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def build_all_textures() -> dict[str, tuple[list[PartSpec], tuple[int, int]]]:
    spider = spec_crystal_spider()
    allocate_and_paint(spider, (128, 128), "261b40", "b474ff", ENTITY_DIR / "crystal_armored_spider.png")

    fox = spec_aether_fox()
    allocate_and_paint(fox, (128, 128), "26325c", "9ef4ff", ENTITY_DIR / "aether_fox.png")

    wolf = spec_rune_wolf()
    allocate_and_paint(wolf, (128, 128), "232730", "38e5f5", ENTITY_DIR / "rune_wolf.png")

    watcher = spec_runic_watcher()
    allocate_and_paint(watcher, (128, 128), "323545", "56ecfa", ENTITY_DIR / "runic_watcher.png")

    wisp = spec_mana_wisp()
    allocate_and_paint(wisp, (128, 128), "5b3ac2", "8af8ff", ENTITY_DIR / "mana_wisp.png")

    # Settler shares identical UV layout across all 3 roles!
    settler_witch = spec_arcane_settler(
        role_motif="settler_face_witch", skin_hex="dfa98c", hair_hex="3b253b",
        coat_hex="3d2859", trim_hex="e3b659", pack_hex="4d3528"
    )
    allocate_and_paint(settler_witch, (128, 128), "3d2859", "e3b659", ENTITY_DIR / "arcane_settler_witch.png")

    settler_goblin = spec_arcane_settler(
        role_motif="settler_face_goblin", skin_hex="7da158", hair_hex="382b20",
        coat_hex="63442d", trim_hex="e0ad48", pack_hex="4a3625"
    )
    allocate_and_paint(settler_goblin, (128, 128), "63442d", "e0ad48", ENTITY_DIR / "arcane_settler_goblin.png")

    settler_researcher = spec_arcane_settler(
        role_motif="settler_face_researcher", skin_hex="dbb094", hair_hex="523628",
        coat_hex="27426b", trim_hex="66e3f5", pack_hex="384254"
    )
    allocate_and_paint(settler_researcher, (128, 128), "27426b", "66e3f5", ENTITY_DIR / "arcane_settler_researcher.png")

    contract_icon(ITEM_DIR / "familiar_contract.png")
    egg_icon(ITEM_DIR / "aether_fox_spawn_egg.png", "26345e", "c6cee5", "8a58e8")
    egg_icon(ITEM_DIR / "rune_wolf_spawn_egg.png", "27282c", "9d7938", "22d6e7")
    egg_icon(ITEM_DIR / "crystal_armored_spider_spawn_egg.png", "201835", "8d50b8", "35d6dc")
    egg_icon(ITEM_DIR / "mana_wisp_spawn_egg.png", "50318b", "48bdd7", "d4eff5")
    egg_icon(ITEM_DIR / "arcane_settler_spawn_egg.png", "303124", "3e5680", "c59a48")
    egg_icon(ITEM_DIR / "runic_watcher_spawn_egg.png", "323545", "a88546", "56ecfa")

    return {
        "crystal_armored_spider": (spider, (128, 128)),
        "aether_fox": (fox, (128, 128)),
        "rune_wolf": (wolf, (128, 128)),
        "runic_watcher": (watcher, (128, 128)),
        "mana_wisp": (wisp, (128, 128)),
        "arcane_settler": (settler_witch, (128, 128)),
    }


if __name__ == "__main__":
    models = build_all_textures()
    for mid, (parts, res) in models.items():
        print(f"Built texture for {mid} ({res[0]}x{res[1]}), top parts={len(parts)}")
