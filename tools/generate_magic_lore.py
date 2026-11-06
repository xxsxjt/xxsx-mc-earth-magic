import json
import pathlib
import struct
import zlib

ROOT = pathlib.Path(__file__).resolve().parents[1] / "neoforge-26.2" / "src" / "main" / "resources"
ASSETS = ROOT / "assets" / "fantasy_earth"
DATA = ROOT / "data"

def write_json(path: pathlib.Path, obj):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(obj, ensure_ascii=False, indent=2) + "\n", encoding="utf-8")

def make_png(width: int, height: int, pixel_fn) -> bytes:
    raw = bytearray()
    for y in range(height):
        raw.append(0)
        for x in range(width):
            r, g, b, a = pixel_fn(x, y)
            raw.extend((max(0, min(255, r)), max(0, min(255, g)), max(0, min(255, b)), max(0, min(255, a))))
    def chunk(tag: bytes, payload: bytes) -> bytes:
        return struct.pack(">I", len(payload)) + tag + payload + struct.pack(">I", zlib.crc32(tag + payload) & 0xFFFFFFFF)
    ihdr = struct.pack(">IIBBBBB", width, height, 8, 6, 0, 0, 0)
    return b"\x89PNG\r\n\x1a\n" + chunk(b"IHDR", ihdr) + chunk(b"IDAT", zlib.compress(bytes(raw), 9)) + chunk(b"IEND", b"")

def write_item_png(path: pathlib.Path, primary, secondary, accent, shape="round"):
    path.parent.mkdir(parents=True, exist_ok=True)
    pr, pg, pb = primary
    sr, sg, sb = secondary
    ar, ag, ab = accent
    def px(x, y):
        cx, cy = x - 7.5, y - 7.5
        dist2 = cx * cx + cy * cy
        if shape == "notes":
            if 3 <= x <= 12 and 2 <= y <= 13:
                if x in (3, 12) or y in (2, 13):
                    return (sr, sg, sb, 255)
                if x == 5 or (y in (5, 8, 10) and 6 <= x <= 10):
                    return (ar, ag, ab, 255)
                return (pr, pg, pb, 255)
            return (0, 0, 0, 0)
        if shape == "scroll":
            if 3 <= x <= 12 and 3 <= y <= 12:
                if y in (3, 12):
                    return (sr, sg, sb, 255)
                if 4 <= x <= 11:
                    if x in (7, 8) and 5 <= y <= 10:
                        return (ar, ag, ab, 255)
                    return (pr, pg, pb, 255)
            return (0, 0, 0, 0)
        if shape == "rune":
            if 3 <= x <= 12 and 2 <= y <= 13 and abs(cx) + abs(cy) <= 7.5:
                if x in (3, 12) or y in (2, 13):
                    return (sr, sg, sb, 255)
                if x in (7, 8) and 4 <= y <= 11 or (y in (5, 10) and 5 <= x <= 10):
                    return (ar, ag, ab, 255)
                return (pr, pg, pb, 255)
            return (0, 0, 0, 0)
        if shape == "rod":
            if abs(x - (15 - y)) <= 1 and 2 <= x <= 13 and 2 <= y <= 13:
                if x >= 9 and y <= 6:
                    return (ar, ag, ab, 255)
                return (pr, pg, pb, 255) if (x + y) % 2 == 0 else (sr, sg, sb, 255)
            return (0, 0, 0, 0)
        if shape == "shard":
            if abs(cx) * 1.4 + abs(cy) <= 6.2:
                if abs(cx) + abs(cy) <= 2.4:
                    return (ar, ag, ab, 255)
                return (pr, pg, pb, 255) if x < 8 else (sr, sg, sb, 255)
            return (0, 0, 0, 0)
        if dist2 > 30:
            return (0, 0, 0, 0)
        if dist2 > 22:
            return (sr, sg, sb, 255)
        if abs(cx) + abs(cy) < 3.2:
            return (ar, ag, ab, 255)
        return (pr, pg, pb, 255)
    path.write_bytes(make_png(16, 16, px))

def write_block_png(path: pathlib.Path, primary, secondary, accent):
    path.parent.mkdir(parents=True, exist_ok=True)
    pr, pg, pb = primary
    sr, sg, sb = secondary
    ar, ag, ab = accent
    def px(x, y):
        if x in (0, 15) or y in (0, 15):
            return (sr, sg, sb, 255)
        if (x in (7, 8) and 3 <= y <= 12) or (y in (4, 11) and 4 <= x <= 11):
            return (ar, ag, ab, 255)
        return (pr, pg, pb, 255) if ((x + y) % 2 == 0) else (max(0, pr - 12), max(0, pg - 12), max(0, pb - 12), 255)
    path.write_bytes(make_png(16, 16, px))

def write_golem_png(path: pathlib.Path):
    path.parent.mkdir(parents=True, exist_ok=True)
    def px(x, y):
        if (x + y) % 11 == 0:
            return (124, 218, 236, 255)
        if x % 16 in (0, 15) or y % 16 in (0, 15):
            return (54, 48, 68, 255)
        return (88, 82, 108, 255) if ((x // 4 + y // 4) % 2 == 0) else (72, 66, 92, 255)
    path.write_bytes(make_png(128, 128, px))

# 1. 23 Knowledge Nodes (15-tree-mystic.md §2)
NODES = [
    {
        "slug": "arcane_initiation", "tier": 1, "gate": "hard",
        "zh": "奥术启蒙", "en": "Arcane Initiation",
        "zh_desc": "建立基础魔力回路并将本地以太场调律为共享法力，开启以太脉冲与护身/水肺札记。",
        "en_desc": "Establish a basic magic circuit and attune local aether into shared mana, unlocking Aether Pulse and ward notes.",
        "requires": [],
        "item_requires": [{"cond": "aether>=20"}],
        "unlocks": [
            {"ability": "fantasy_earth:spell_attunement"},
            {"ability": "fantasy_earth:spell_casting"},
            {"ability": "fantasy_earth:spell_graph_builtin"},
            {"item": "fantasy_earth:arcane_body_ward_notes"},
            {"item": "fantasy_earth:arcane_breath_ward_notes"}
        ],
        "literature": ["notes", "arcane_notes"],
        "teachers": ["fantasy_earth:arcane_settler"],
        "research": {"items": [{"ingredient": "fantasy_earth:arcane_dust", "count": 2}, {"ingredient": "minecraft:book", "count": 1}], "ticks": 600},
        "fail_codes": ["aether_low"]
    },
    {
        "slug": "arcane_research_2", "tier": 2, "gate": "theory",
        "zh": "奥术研究二阶", "en": "Arcane Research II",
        "zh_desc": "深化魔力回路理论与导能测算，为高阶法器与进阶学派奠定基础。",
        "en_desc": "Deepen circuit theory and aether conduction analysis, laying the foundation for foci and higher schools.",
        "requires": [{"all": "fantasy_earth:mag/arcane_initiation"}],
        "item_requires": [],
        "unlocks": [],
        "literature": ["notes", "arcane_notes"],
        "teachers": ["fantasy_earth:arcane_settler"],
        "research": {"items": [{"ingredient": "fantasy_earth:arcane_dust", "count": 4}, {"ingredient": "fantasy_earth:aether_shard", "count": 2}], "ticks": 1200},
        "fail_codes": []
    },
    {
        "slug": "arcane_research_3", "tier": 3, "gate": "soft",
        "zh": "奥术研究三阶", "en": "Arcane Research III",
        "zh_desc": "掌握高密度魔力释放结构，解锁塑能札记。",
        "en_desc": "Master high-density mana projection structures, unlocking Evocation Notes.",
        "requires": [{"all": "fantasy_earth:mag/arcane_research_2"}],
        "item_requires": [],
        "unlocks": [{"item": "fantasy_earth:evocation_notes"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_crystal", "count": 1}, {"ingredient": "fantasy_earth:rune_power", "count": 1}], "ticks": 2400},
        "fail_codes": []
    },
    {
        "slug": "arcane_research_4", "tier": 4, "gate": "soft",
        "zh": "奥术研究四阶", "en": "Arcane Research IV",
        "zh_desc": "解析光线折射与认知干涉回路，解锁幻术札记。",
        "en_desc": "Analyze light refraction and cognitive interference circuits, unlocking Illusion Notes.",
        "requires": [{"all": "fantasy_earth:mag/arcane_research_3"}],
        "item_requires": [],
        "unlocks": [{"item": "fantasy_earth:illusion_notes"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_crystal", "count": 2}, {"ingredient": "fantasy_earth:rune_flow", "count": 1}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "arcane_research_5", "tier": 5, "gate": "soft",
        "zh": "奥术研究五阶", "en": "Arcane Research V",
        "zh_desc": "在高浓度以太场中构建空间锚定方程，解锁传送札记。",
        "en_desc": "Formulate spatial anchoring equations in dense aether fields, unlocking Translocation Notes.",
        "requires": [{"all": "fantasy_earth:mag/arcane_research_4"}],
        "item_requires": [{"cond": "aether>=60"}],
        "unlocks": [{"item": "fantasy_earth:translocation_notes"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_crystal", "count": 2}, {"ingredient": "fantasy_earth:rune_flow", "count": 2}, {"ingredient": "fantasy_earth:aether_lens", "count": 1}], "ticks": 6000},
        "fail_codes": ["aether_low"]
    },
    {
        "slug": "aether_crystals", "tier": 1, "gate": "soft",
        "zh": "以太晶体", "en": "Aether Crystals",
        "zh_desc": "认识聚魔水晶、以太碎片与魔尘，掌握晶体充放能与田野手册。",
        "en_desc": "Study aether crystals, shards and arcane dust, unlocking crystal charging and field notebooks.",
        "requires": [],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:aether_crystal"},
            {"block": "fantasy_earth:aether_crystal_cluster"},
            {"item": "fantasy_earth:aether_crystal_cluster"},
            {"item": "fantasy_earth:arcane_dust"},
            {"item": "fantasy_earth:aether_shard"},
            {"ability": "fantasy_earth:aether_crystal_charge"},
            {"item": "fantasy_earth:field_arcane_notebook"},
            {"item": "fantasy_earth:arcane_initiation_notes"},
            {"item": "fantasy_earth:arcane_research_notes"}
        ],
        "literature": ["notes"],
        "teachers": ["fantasy_earth:arcane_settler"],
        "research": {"items": [{"ingredient": "minecraft:amethyst_shard", "count": 4}, {"ingredient": "minecraft:glowstone_dust", "count": 2}], "ticks": 600},
        "fail_codes": []
    },
    {
        "slug": "body_ward", "tier": 2, "gate": "hard",
        "zh": "护体", "en": "Body Ward",
        "zh_desc": "构筑贴身奥术护盾，缓冲冲击与代谢消耗，解锁护体学派。",
        "en_desc": "Weave a close-fitting arcane ward that buffers impact and fatigue, unlocking the Body Ward school.",
        "requires": [{"all": "fantasy_earth:mag/arcane_initiation"}],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:spell_ward"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_shard", "count": 2}, {"ingredient": "minecraft:iron_ingot", "count": 4}], "ticks": 1200},
        "fail_codes": []
    },
    {
        "slug": "breath_ward", "tier": 2, "gate": "hard",
        "zh": "护息", "en": "Breath Ward",
        "zh_desc": "以魔力维持呼吸节律并延长水下闭气，解锁护息学派。",
        "en_desc": "Sustain respiratory rhythm with mana for underwater breathing, unlocking the Breath Ward school.",
        "requires": [{"all": "fantasy_earth:mag/arcane_initiation"}],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:spell_breath"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_shard", "count": 2}, {"ingredient": "minecraft:kelp", "count": 8}], "ticks": 1200},
        "fail_codes": []
    },
    {
        "slug": "rune_carving", "tier": 2, "gate": "soft",
        "zh": "刻符", "en": "Rune Carving",
        "zh_desc": "调配符文墨水并在铜片与符文石上刻写导能回路，解锁符文刻台与空白符文。",
        "en_desc": "Prepare rune ink and carve conducting circuits onto copper plates and rune stones, unlocking the Rune Carving Table.",
        "requires": [{"all": "fantasy_earth:mag/aether_crystals"}],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:rune_ink"},
            {"item": "fantasy_earth:rune_copper_plate"},
            {"block": "fantasy_earth:rune_stone"},
            {"item": "fantasy_earth:rune_stone"},
            {"item": "fantasy_earth:rune_blank"},
            {"block": "fantasy_earth:rune_carving_table"},
            {"item": "fantasy_earth:rune_carving_table"},
            {"ability": "fantasy_earth:rune_carving"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "minecraft:stone", "count": 8}, {"ingredient": "fantasy_earth:arcane_dust", "count": 4}, {"ingredient": "minecraft:ink_sac", "count": 2}], "ticks": 1200},
        "fail_codes": []
    },
    {
        "slug": "rune_combination", "tier": 3, "gate": "soft",
        "zh": "符文组合", "en": "Rune Combination",
        "zh_desc": "掌握力、护、流、缚四类基础符文的刻写、升阶与三槽协同规则。",
        "en_desc": "Master the inscription, tier upgrading and three-slot synergy of Power, Guard, Flow and Bind runes.",
        "requires": [{"all": "fantasy_earth:mag/rune_carving"}],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:rune_power"},
            {"item": "fantasy_earth:rune_guard"},
            {"item": "fantasy_earth:rune_flow"},
            {"item": "fantasy_earth:rune_bind"},
            {"ability": "fantasy_earth:rune_combination"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:rune_blank", "count": 4}, {"ingredient": "fantasy_earth:rune_ink", "count": 4}], "ticks": 2400},
        "fail_codes": []
    },
    {
        "slug": "jade_runes", "tier": 4, "gate": "soft",
        "zh": "玉质符文", "en": "Jade Runes",
        "zh_desc": "结合玉石温润导性与符文刻写，制造四阶玉质符文与玉质法器壳。",
        "en_desc": "Combine nephrite and jadeite resonance with rune carving to craft tier-4 Jade Runes and Jade Focus Housings.",
        "requires": [
            {"all": "fantasy_earth:mag/rune_combination"},
            {"any": ["earth_on_minecraft:geo/gemology", "fantasy_earth:mag/aether_sensing"]}
        ],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:rune_jade"},
            {"item": "fantasy_earth:jade_focus_housing"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:rune_blank", "count": 4}, {"ingredient": "fantasy_earth:rune_ink", "count": 4}, {"ingredient": "minecraft:emerald", "count": 1}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "gem_socketing", "tier": 4, "gate": "soft",
        "zh": "宝石镶嵌", "en": "Gem Socketing",
        "zh_desc": "将刻面宝石嵌入奥术法器槽位，按宝石品种与品质强化各学派法术表现。",
        "en_desc": "Socket cut gems into Arcane Foci to amplify spell damage, duration and range based on gem species and grade.",
        "requires": [
            {"all": "fantasy_earth:mag/rune_combination"},
            {"any": ["earth_on_minecraft:geo/lapidary", "fantasy_earth:mag/aether_sensing"]}
        ],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:gem_socketing"}],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "minecraft:amethyst_shard", "count": 4}, {"ingredient": "minecraft:emerald", "count": 2}, {"ingredient": "fantasy_earth:rune_ink", "count": 2}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "rituals", "tier": 3, "gate": "soft",
        "zh": "仪式", "en": "Arcane Rituals",
        "zh_desc": "在夜间布置仪式基座、蜡烛、供碗与法阵标记，引导高能以太转化。",
        "en_desc": "Arrange ritual pedestals, candles, offering bowls and chalk markers at night to channel high-energy aether.",
        "requires": [
            {"all": "fantasy_earth:mag/aether_crystals"},
            {"all": "fantasy_earth:mag/rune_carving"}
        ],
        "item_requires": [{"cond": "night"}],
        "unlocks": [
            {"item": "fantasy_earth:ritual_chalk"},
            {"block": "fantasy_earth:ritual_pedestal"},
            {"item": "fantasy_earth:ritual_pedestal"},
            {"item": "fantasy_earth:dormant_ritual_core"},
            {"item": "fantasy_earth:inscribed_ritual_core"},
            {"block": "fantasy_earth:ritual_candle"},
            {"item": "fantasy_earth:ritual_candle"},
            {"block": "fantasy_earth:offering_bowl"},
            {"item": "fantasy_earth:offering_bowl"},
            {"block": "fantasy_earth:ritual_circle_marker"},
            {"item": "fantasy_earth:ritual_circle_marker"},
            {"ability": "fantasy_earth:ritual_formation"},
            {"ability": "fantasy_earth:ritual_execution"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "minecraft:candle", "count": 4}, {"ingredient": "fantasy_earth:arcane_dust", "count": 4}, {"ingredient": "minecraft:calcite", "count": 4}], "ticks": 2400},
        "fail_codes": ["needs_night"]
    },
    {
        "slug": "foci_and_scrolls", "tier": 5, "gate": "soft",
        "zh": "法器与卷轴", "en": "Foci and Scrolls",
        "zh_desc": "制造奥术法器、法术卷轴与冥想垫，支持装载自定义术式图。",
        "en_desc": "Craft Arcane Foci, Spell Scrolls and Focus Mats, enabling custom spell graph execution.",
        "requires": [
            {"all": "fantasy_earth:mag/arcane_research_2"},
            {"all": "fantasy_earth:mag/aether_storage"}
        ],
        "item_requires": [],
        "unlocks": [
            {"block": "fantasy_earth:arcane_focus_mat"},
            {"item": "fantasy_earth:arcane_focus_mat"},
            {"item": "fantasy_earth:arcane_focus"},
            {"item": "fantasy_earth:spell_scroll"},
            {"ability": "fantasy_earth:spell_graph_custom"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_lens", "count": 1}, {"ingredient": "fantasy_earth:rune_copper_plate", "count": 2}, {"ingredient": "minecraft:paper", "count": 4}], "ticks": 6000},
        "fail_codes": []
    },
    {
        "slug": "familiars", "tier": 2, "gate": "soft",
        "zh": "魔法生物契约", "en": "Familiar Contracts",
        "zh_desc": "以魔尘与契约书安抚并缔结以太狐、符文狼与法力精怪。",
        "en_desc": "Calm and bind Aether Foxes, Runebound Wolves and Mana Wisps using arcane dust and Familiar Contracts.",
        "requires": [{"all": "fantasy_earth:mag/aether_crystals"}],
        "item_requires": [],
        "unlocks": [{"item": "fantasy_earth:familiar_contract"}],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_shard", "count": 4}, {"ingredient": "minecraft:bone", "count": 4}], "ticks": 1200},
        "fail_codes": []
    },
    {
        "slug": "evocation", "tier": 4, "gate": "hard",
        "zh": "塑能", "en": "Evocation",
        "zh_desc": "凝练奥术飞弹轰击前方目标，需法器装配二阶以上力之符文。",
        "en_desc": "Project Arcane Missiles at targets ahead; requires a focus fitted with a tier 2+ Power Rune.",
        "requires": [
            {"all": "fantasy_earth:mag/arcane_research_3"},
            {"all": "fantasy_earth:mag/rune_combination"}
        ],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:spell_evocation"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:rune_power", "count": 2}, {"ingredient": "minecraft:blaze_powder", "count": 2}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "illusion", "tier": 5, "gate": "hard",
        "zh": "幻术", "en": "Illusion",
        "zh_desc": "折射以太制造镜像隐匿身形，需法器装配二阶以上流之符文。",
        "en_desc": "Bend aether to weave mirror images and conceal yourself; requires a focus fitted with a tier 2+ Flow Rune.",
        "requires": [
            {"all": "fantasy_earth:mag/arcane_research_4"},
            {"all": "fantasy_earth:mag/rune_combination"}
        ],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:spell_illusion"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:rune_flow", "count": 2}, {"ingredient": "minecraft:fermented_spider_eye", "count": 2}], "ticks": 6000},
        "fail_codes": []
    },
    {
        "slug": "translocation", "tier": 6, "gate": "hard",
        "zh": "传送", "en": "Translocation",
        "zh_desc": "折叠局部以太实现视线内短距闪现，需法器装配三阶以上流之符文。",
        "en_desc": "Fold local aether for short-range line-of-sight blink; requires a focus fitted with a tier 3+ Flow Rune.",
        "requires": [
            {"all": "fantasy_earth:mag/arcane_research_5"},
            {"all": "fantasy_earth:mag/rune_combination"}
        ],
        "item_requires": [],
        "unlocks": [{"ability": "fantasy_earth:spell_translocation"}],
        "literature": ["notes", "arcane_notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:rune_flow", "count": 2}, {"ingredient": "minecraft:ender_pearl", "count": 4}], "ticks": 7200},
        "fail_codes": []
    },
    {
        "slug": "alchemy", "tier": 3, "gate": "soft",
        "zh": "炼金术", "en": "Arcane Alchemy",
        "zh_desc": "利用炼金台与以太场温控提纯晶化魔盐与以太玻璃。",
        "en_desc": "Refine Crystallized Mana Salt and Aether Glass in temperature-controlled Alchemy Tables.",
        "requires": [
            {"all": "fantasy_earth:mag/aether_crystals"},
            {"any": ["earth_on_minecraft:che/inorganic_chemistry", "fantasy_earth:mag/aether_sensing"]}
        ],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:crystallized_mana_salt"},
            {"item": "fantasy_earth:aether_glass"},
            {"block": "fantasy_earth:alchemy_table"},
            {"item": "fantasy_earth:alchemy_table"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:arcane_dust", "count": 4}, {"ingredient": "minecraft:glass", "count": 4}, {"ingredient": "minecraft:redstone", "count": 4}], "ticks": 2400},
        "fail_codes": []
    },
    {
        "slug": "aether_storage", "tier": 4, "gate": "soft",
        "zh": "以太储能", "en": "Aether Storage",
        "zh_desc": "制造以太透镜、以太导管与大容量以太电池，构建魔力储运网络。",
        "en_desc": "Build Aether Lenses, Aether Conduits and high-capacity Aether Batteries for arcane energy storage.",
        "requires": [
            {"all": "fantasy_earth:mag/rune_carving"},
            {"all": "fantasy_earth:mag/alchemy"}
        ],
        "item_requires": [],
        "unlocks": [
            {"item": "fantasy_earth:aether_lens"},
            {"item": "fantasy_earth:aether_conduit"},
            {"block": "fantasy_earth:aether_battery"},
            {"item": "fantasy_earth:aether_battery"}
        ],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_glass", "count": 4}, {"ingredient": "fantasy_earth:rune_copper_plate", "count": 2}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "constructs", "tier": 6, "gate": "hard",
        "zh": "构装", "en": "Arcane Constructs",
        "zh_desc": "通过成形仪式唤醒构装核心，召唤替主人分担伤害的契约魔像。",
        "en_desc": "Awaken Construct Cores in formed rituals to summon Contract Golems that share incoming damage.",
        "requires": [
            {"all": "fantasy_earth:mag/rituals"},
            {"all": "fantasy_earth:mag/aether_storage"}
        ],
        "item_requires": [{"cond": "aether>=50"}],
        "unlocks": [{"item": "fantasy_earth:construct_core"}],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:inscribed_ritual_core", "count": 1}, {"ingredient": "fantasy_earth:rune_guard", "count": 2}, {"ingredient": "minecraft:iron_block", "count": 2}], "ticks": 7200},
        "fail_codes": ["aether_low"]
    },
    {
        "slug": "aether_sensing", "tier": 2, "gate": "theory",
        "zh": "以太辨物（自研）", "en": "Aether Material Sensing",
        "zh_desc": "不借助地质与化学体系，直接凭以太谐振辨析矿石、玉石与药剂反应。",
        "en_desc": "Discern mineral, jade and alchemical properties directly through aether resonance without external sciences.",
        "requires": [{"all": "fantasy_earth:mag/aether_crystals"}],
        "item_requires": [],
        "unlocks": [],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_shard", "count": 8}, {"ingredient": "minecraft:amethyst_shard", "count": 8}, {"ingredient": "minecraft:paper", "count": 8}], "ticks": 4800},
        "fail_codes": []
    },
    {
        "slug": "magic_mastery", "tier": 7, "gate": "cap",
        "zh": "魔法精研", "en": "Magic Mastery",
        "zh_desc": "贯通六大学派、玉质符文、宝石镶嵌与构装仪式，解锁魔法学传世品质。",
        "en_desc": "Master all six arcane schools, jade runes, gem socketing and constructs to unlock heirloom-grade magic creations.",
        "requires": [
            {"all": "fantasy_earth:mag/body_ward"},
            {"all": "fantasy_earth:mag/breath_ward"},
            {"all": "fantasy_earth:mag/jade_runes"},
            {"all": "fantasy_earth:mag/gem_socketing"},
            {"all": "fantasy_earth:mag/foci_and_scrolls"},
            {"all": "fantasy_earth:mag/familiars"},
            {"all": "fantasy_earth:mag/evocation"},
            {"all": "fantasy_earth:mag/illusion"},
            {"all": "fantasy_earth:mag/translocation"},
            {"all": "fantasy_earth:mag/constructs"}
        ],
        "item_requires": [],
        "unlocks": [{"capstone": "earth_lore:mag"}],
        "literature": ["notes"],
        "teachers": [],
        "research": {"items": [{"ingredient": "fantasy_earth:aether_battery", "count": 1}, {"ingredient": "fantasy_earth:arcane_focus", "count": 1}, {"ingredient": "fantasy_earth:spell_scroll", "count": 4}], "ticks": 9600},
        "fail_codes": []
    }
]

for n in NODES:
    slug = n["slug"]
    node_json = {
        "discipline": "earth_lore:mag",
        "tier": n["tier"],
        "gate": n["gate"],
        "requires": n["requires"],
        "item_requires": n["item_requires"],
        "unlocks": n["unlocks"],
        "literature": n["literature"],
        "teachers": n["teachers"],
        "research": n["research"],
        "fail_codes": n["fail_codes"],
        "name_key": f"lore.fantasy_earth.mag.{slug}",
        "desc_key": f"lore.fantasy_earth.mag.{slug}.desc"
    }
    write_json(DATA / "fantasy_earth" / "earth_lore" / "knowledge_node" / "mag" / f"{slug}.json", node_json)

# 2. carrier_forms.json (03 册 §6 札记规则 2)
write_json(DATA / "fantasy_earth" / "earth_lore" / "carrier_forms.json", {
    "forms": [
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:arcane_initiation_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/arcane_initiation"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:arcane_body_ward_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/body_ward"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:arcane_breath_ward_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/breath_ward"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:evocation_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/evocation"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:illusion_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/illusion"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:translocation_notes",
            "discipline": "earth_lore:mag",
            "nodes": ["fantasy_earth:mag/translocation"]
        },
        {
            "carrier": "earth_lore:blank_notes",
            "result": "fantasy_earth:arcane_research_notes",
            "discipline": "earth_lore:mag"
        }
    ]
})

# 3. teaching.json & tags
teaching_map = {
    "replace": False,
    "values": {
        "fantasy_earth:arcane_settler": [
            {"node": "fantasy_earth:mag/arcane_initiation", "price": 8, "max_uses": 3},
            {"node": "fantasy_earth:mag/arcane_research_2", "price": 12, "max_uses": 3},
            {"node": "fantasy_earth:mag/aether_crystals", "price": 8, "max_uses": 3}
        ]
    }
}
write_json(DATA / "earth_lore" / "data_maps" / "entity_type" / "teaching.json", teaching_map)
write_json(DATA / "fantasy_earth" / "data_maps" / "entity_type" / "earth_lore" / "teaching.json", teaching_map)

write_json(DATA / "earth_lore" / "tags" / "item" / "custom_study.json", {
    "replace": False,
    "values": [
        "fantasy_earth:arcane_initiation_notes",
        "fantasy_earth:arcane_body_ward_notes",
        "fantasy_earth:arcane_breath_ward_notes",
        "fantasy_earth:evocation_notes",
        "fantasy_earth:illusion_notes",
        "fantasy_earth:translocation_notes",
        "fantasy_earth:arcane_research_notes"
    ]
})
write_json(DATA / "earth_lore" / "tags" / "block" / "records_owner.json", {
    "replace": False,
    "values": [
        "fantasy_earth:alchemy_table",
        "fantasy_earth:rune_carving_table",
        "fantasy_earth:ritual_pedestal"
    ]
})
write_json(DATA / "fantasy_earth" / "tags" / "entity_type" / "arcane_constructs.json", {
    "replace": False,
    "values": [
        "fantasy_earth:runic_watcher",
        "fantasy_earth:contract_golem"
    ]
})
write_json(DATA / "minecraft" / "tags" / "block" / "mineable" / "pickaxe.json", {
    "replace": False,
    "values": [
        "fantasy_earth:alchemy_table",
        "fantasy_earth:rune_carving_table",
        "fantasy_earth:ritual_pedestal",
        "fantasy_earth:aether_crystal_cluster",
        "fantasy_earth:arcane_focus_mat",
        "fantasy_earth:aether_battery",
        "fantasy_earth:rune_stone",
        "fantasy_earth:offering_bowl"
    ]
})
write_json(DATA / "minecraft" / "tags" / "block" / "needs_stone_tool.json", {
    "replace": False,
    "values": [
        "fantasy_earth:rune_carving_table",
        "fantasy_earth:ritual_pedestal",
        "fantasy_earth:aether_crystal_cluster",
        "fantasy_earth:aether_battery",
        "fantasy_earth:rune_stone"
    ]
})

# 4. Blockstates, models, textures, loot tables
write_json(ASSETS / "blockstates" / "arcane_focus_mat.json", {
    "variants": {
        "facing=north,occupied=false": {"model": "fantasy_earth:block/arcane_focus_mat"},
        "facing=east,occupied=false": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 90},
        "facing=south,occupied=false": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 180},
        "facing=west,occupied=false": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 270},
        "facing=north,occupied=true": {"model": "fantasy_earth:block/arcane_focus_mat"},
        "facing=east,occupied=true": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 90},
        "facing=south,occupied=true": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 180},
        "facing=west,occupied=true": {"model": "fantasy_earth:block/arcane_focus_mat", "y": 270}
    }
})

write_json(ASSETS / "blockstates" / "aether_battery.json", {
    "variants": {
        f"charge_step={s}": {"model": "fantasy_earth:block/aether_battery"} for s in range(7)
    }
})
write_json(ASSETS / "blockstates" / "rune_stone.json", {
    "variants": {"": {"model": "fantasy_earth:block/rune_stone"}}
})
write_json(ASSETS / "blockstates" / "ritual_candle.json", {
    "variants": {
        "lit=false": {"model": "fantasy_earth:block/ritual_candle"},
        "lit=true": {"model": "fantasy_earth:block/ritual_candle"}
    }
})
write_json(ASSETS / "blockstates" / "offering_bowl.json", {
    "variants": {
        "filled=false": {"model": "fantasy_earth:block/offering_bowl"},
        "filled=true": {"model": "fantasy_earth:block/offering_bowl"}
    }
})
write_json(ASSETS / "blockstates" / "ritual_circle_marker.json", {
    "variants": {"": {"model": "fantasy_earth:block/ritual_circle_marker"}}
})

NEW_BLOCKS = {
    "aether_battery": ((78, 128, 168), (44, 72, 98), (156, 236, 255)),
    "rune_stone": ((96, 92, 112), (56, 52, 68), (186, 138, 248)),
    "ritual_candle": ((232, 218, 176), (158, 138, 96), (255, 198, 78)),
    "offering_bowl": ((112, 108, 124), (64, 60, 74), (212, 174, 102)),
    "ritual_circle_marker": ((228, 224, 236), (148, 138, 168), (192, 148, 252))
}

for b_slug, (c1, c2, c3) in NEW_BLOCKS.items():
    write_json(ASSETS / "models" / "block" / f"{b_slug}.json", {
        "parent": "minecraft:block/cube_all",
        "textures": {"all": f"fantasy_earth:block/{b_slug}"}
    })
    write_json(ASSETS / "models" / "item" / f"{b_slug}.json", {
        "parent": f"fantasy_earth:block/{b_slug}"
    })
    write_json(ASSETS / "items" / f"{b_slug}.json", {
        "model": {"type": "minecraft:model", "model": f"fantasy_earth:item/{b_slug}"}
    })
    write_block_png(ASSETS / "textures" / "block" / f"{b_slug}.png", c1, c2, c3)
    write_json(DATA / "fantasy_earth" / "loot_table" / "blocks" / f"{b_slug}.json", {
        "type": "minecraft:block",
        "pools": [{
            "rolls": 1.0,
            "entries": [{"type": "minecraft:item", "name": f"fantasy_earth:{b_slug}"}],
            "conditions": [{"condition": "minecraft:survives_explosion"}]
        }],
        "random_sequence": f"fantasy_earth:blocks/{b_slug}"
    })

# Update aether_crystal_cluster loot table to drop aether_shard 1..3 + fortune (F-002)
write_json(DATA / "fantasy_earth" / "loot_table" / "blocks" / "aether_crystal_cluster.json", {
    "type": "minecraft:block",
    "pools": [{
        "rolls": 1.0,
        "entries": [{
            "type": "minecraft:item",
            "name": "fantasy_earth:aether_shard",
            "functions": [
                {"function": "minecraft:set_count", "count": {"type": "minecraft:uniform", "min": 1.0, "max": 3.0}},
                {"function": "minecraft:apply_bonus", "enchantment": "minecraft:fortune", "formula": "minecraft:ore_drops"},
                {"function": "minecraft:explosion_decay"}
            ]
        }]
    }],
    "random_sequence": "fantasy_earth:blocks/aether_crystal_cluster"
})

# 5. New items: item definitions, models, textures
NEW_ITEMS = {
    "evocation_notes": ((176, 78, 92), (96, 38, 48), (255, 168, 112), "notes"),
    "illusion_notes": ((118, 92, 186), (62, 46, 108), (198, 178, 255), "notes"),
    "translocation_notes": ((72, 148, 168), (38, 82, 96), (156, 244, 255), "notes"),
    "arcane_research_notes": ((132, 104, 168), (72, 54, 96), (224, 198, 255), "notes"),
    "aether_shard": ((118, 206, 244), (58, 124, 168), (224, 250, 255), "shard"),
    "aether_lens": ((164, 228, 248), (188, 126, 74), (242, 254, 255), "round"),
    "aether_conduit": ((184, 118, 72), (96, 58, 34), (132, 224, 246), "rod"),
    "rune_blank": ((138, 134, 148), (78, 74, 86), (178, 174, 188), "rune"),
    "rune_power": ((142, 118, 148), (82, 58, 88), (255, 132, 74), "rune"),
    "rune_guard": ((118, 136, 156), (64, 78, 94), (148, 224, 255), "rune"),
    "rune_flow": ((108, 152, 158), (56, 86, 92), (124, 248, 224), "rune"),
    "rune_bind": ((136, 116, 158), (74, 58, 94), (218, 148, 255), "rune"),
    "rune_jade": ((92, 192, 148), (46, 108, 82), (212, 255, 228), "rune"),
    "jade_focus_housing": ((84, 184, 138), (44, 102, 74), (238, 212, 128), "round"),
    "inscribed_ritual_core": ((128, 88, 176), (68, 42, 102), (168, 238, 255), "round"),
    "construct_core": ((108, 118, 142), (56, 62, 78), (255, 196, 92), "round"),
    "arcane_focus": ((138, 102, 178), (74, 52, 102), (144, 238, 255), "rod"),
    "spell_scroll": ((236, 222, 184), (156, 128, 78), (168, 104, 238), "scroll")
}

for slug, (c1, c2, c3, shape) in NEW_ITEMS.items():
    write_json(ASSETS / "items" / f"{slug}.json", {
        "model": {"type": "minecraft:model", "model": f"fantasy_earth:item/{slug}"}
    })
    parent = "minecraft:item/handheld" if shape == "rod" else "minecraft:item/generated"
    write_json(ASSETS / "models" / "item" / f"{slug}.json", {
        "parent": parent,
        "textures": {"layer0": f"fantasy_earth:item/{slug}"}
    })
    write_item_png(ASSETS / "textures" / "item" / f"{slug}.png", c1, c2, c3, shape)

write_golem_png(ASSETS / "textures" / "entity" / "contract_golem.png")

# 6. Built-in Spell Graph JSONs for evocation, illusion, translocation
SPELL_GRAPHS = {
    "evocation": 14,
    "illusion": 16,
    "translocation": 18
}
for g_slug, mp in SPELL_GRAPHS.items():
    write_json(DATA / "fantasy_earth" / "spell_graph" / f"{g_slug}.json", {
        "id": f"fantasy_earth:{g_slug}",
        "sources": ["player_mana", "local_aether"],
        "nodes": [
            {"kind": "arcane_focus", "input": "energy"},
            {"kind": "ward_shaper", "input": "energy"},
            {"kind": "self_target", "input": "control"},
            {"kind": "ward_effect", "input": "target", "cost_aeu": 0, "cost_mana": mp},
            {"kind": "aether_disturbance_sink", "input": "target"}
        ]
    })

# 7. Crafting Recipes (03-fantasy-earth.md)
RECIPES = {
    "field_arcane_notebook": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": ["minecraft:book", "fantasy_earth:arcane_dust"],
        "result": {"id": "fantasy_earth:field_arcane_notebook", "count": 1}
    },
    "aether_shard": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": ["fantasy_earth:aether_crystal"],
        "result": {"id": "fantasy_earth:aether_shard", "count": 2}
    },
    "aether_lens": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "pattern": [" G ", "GSG", " P "],
        "key": {
            "G": "fantasy_earth:aether_glass",
            "S": "fantasy_earth:aether_shard",
            "P": "fantasy_earth:rune_copper_plate"
        },
        "result": {"id": "fantasy_earth:aether_lens", "count": 1}
    },
    "aether_conduit": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": [
            "fantasy_earth:aether_glass",
            "minecraft:copper_ingot",
            "fantasy_earth:rune_ink"
        ],
        "result": {"id": "fantasy_earth:aether_conduit", "count": 2}
    },
    "aether_battery_from_earth": {
        "neoforge:conditions": [{"type": "neoforge:mod_loaded", "modid": "earth_on_minecraft"}],
        "type": "minecraft:crafting_shaped",
        "category": "redstone",
        "pattern": ["PCP", "CEC", "PCP"],
        "key": {
            "C": "fantasy_earth:aether_crystal",
            "P": "fantasy_earth:rune_copper_plate",
            "E": "earth_on_minecraft:copper_power_cable"
        },
        "result": {"id": "fantasy_earth:aether_battery", "count": 1}
    },
    "aether_battery": {
        "neoforge:conditions": [{"type": "neoforge:not", "value": {"type": "neoforge:mod_loaded", "modid": "earth_on_minecraft"}}],
        "type": "minecraft:crafting_shaped",
        "category": "redstone",
        "pattern": ["PCP", "CEC", "PCP"],
        "key": {
            "C": "fantasy_earth:aether_crystal",
            "P": "fantasy_earth:rune_copper_plate",
            "E": "fantasy_earth:aether_conduit"
        },
        "result": {"id": "fantasy_earth:aether_battery", "count": 1}
    },
    "rune_stone": {
        "type": "minecraft:crafting_shapeless",
        "category": "building",
        "ingredients": ["minecraft:stone", "minecraft:stone", "fantasy_earth:arcane_dust"],
        "result": {"id": "fantasy_earth:rune_stone", "count": 2}
    },
    "jade_focus_housing_from_earth": {
        "neoforge:conditions": [{"type": "neoforge:mod_loaded", "modid": "earth_on_minecraft"}],
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["NJN", "PCP", " N "],
        "key": {
            "N": "#earth_on_minecraft:gems/nephrite",
            "J": "fantasy_earth:aether_crystal",
            "P": "fantasy_earth:rune_copper_plate",
            "C": "fantasy_earth:rune_copper_plate"
        },
        "result": {"id": "fantasy_earth:jade_focus_housing", "count": 1}
    },
    "jade_focus_housing": {
        "neoforge:conditions": [{"type": "neoforge:not", "value": {"type": "neoforge:mod_loaded", "modid": "earth_on_minecraft"}}],
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["NJN", "PCP", " N "],
        "key": {
            "N": "minecraft:emerald",
            "J": "fantasy_earth:aether_crystal",
            "P": "fantasy_earth:rune_copper_plate",
            "C": "fantasy_earth:rune_copper_plate"
        },
        "result": {"id": "fantasy_earth:jade_focus_housing", "count": 1}
    },
    "dormant_ritual_core": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": [
            "fantasy_earth:rune_copper_plate",
            "fantasy_earth:rune_copper_plate",
            "fantasy_earth:aether_shard",
            "fantasy_earth:arcane_dust",
            "fantasy_earth:arcane_dust"
        ],
        "result": {"id": "fantasy_earth:dormant_ritual_core", "count": 1}
    },
    "inscribed_ritual_core": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": [
            "fantasy_earth:dormant_ritual_core",
            "fantasy_earth:rune_power",
            "fantasy_earth:rune_flow",
            "fantasy_earth:rune_ink",
            "fantasy_earth:rune_ink"
        ],
        "result": {"id": "fantasy_earth:inscribed_ritual_core", "count": 1}
    },
    "ritual_candle": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": ["minecraft:candle", "fantasy_earth:arcane_dust"],
        "result": {"id": "fantasy_earth:ritual_candle", "count": 2}
    },
    "offering_bowl": {
        "type": "minecraft:crafting_shaped",
        "category": "misc",
        "pattern": ["S S", " R "],
        "key": {
            "S": "minecraft:stone_bricks",
            "R": "fantasy_earth:rune_ink"
        },
        "result": {"id": "fantasy_earth:offering_bowl", "count": 1}
    },
    "ritual_circle_marker": {
        "type": "minecraft:crafting_shapeless",
        "category": "misc",
        "ingredients": ["fantasy_earth:ritual_chalk"],
        "result": {"id": "fantasy_earth:ritual_circle_marker", "count": 2}
    },
    "arcane_focus": {
        "type": "minecraft:crafting_shaped",
        "category": "equipment",
        "pattern": ["  L", " P ", "C P"],
        "key": {
            "L": "fantasy_earth:aether_lens",
            "P": "fantasy_earth:rune_copper_plate",
            "C": "fantasy_earth:aether_crystal"
        },
        "result": {"id": "fantasy_earth:arcane_focus", "count": 1}
    },
    "spell_scroll": {
        "type": "minecraft:crafting_shapeless",
        "category": "equipment",
        "ingredients": [
            "minecraft:paper",
            "minecraft:paper",
            "fantasy_earth:rune_ink",
            "fantasy_earth:aether_shard"
        ],
        "result": {"id": "fantasy_earth:spell_scroll", "count": 2}
    }
}
for r_name, r_json in RECIPES.items():
    write_json(DATA / "fantasy_earth" / "recipe" / f"{r_name}.json", r_json)

# 8. Language files (zh_cn.json & en_us.json)
zh_path = ASSETS / "lang" / "zh_cn.json"
en_path = ASSETS / "lang" / "en_us.json"
zh = json.loads(zh_path.read_text(encoding="utf-8"))
en = json.loads(en_path.read_text(encoding="utf-8"))

zh_updates = {
    "itemGroup.fantasy_earth": "地球：魔法学",
    "key.categories.fantasy_earth.controls": "地球：魔法学",
    "guide.fantasy_earth.line0": "[地球：魔法学] 学科化测试版已装载",
    "screen.fantasy_earth.handbook.title": "地球：魔法学手册",
    "item.fantasy_earth.evocation_notes": "塑能奥术札记",
    "item.fantasy_earth.illusion_notes": "幻术奥术札记",
    "item.fantasy_earth.translocation_notes": "传送奥术札记",
    "item.fantasy_earth.arcane_research_notes": "奥术研究札记",
    "item.fantasy_earth.aether_shard": "以太碎片",
    "item.fantasy_earth.aether_lens": "以太透镜",
    "item.fantasy_earth.aether_conduit": "以太导管",
    "item.fantasy_earth.aether_battery": "以太电池",
    "item.fantasy_earth.rune_stone": "符文石",
    "item.fantasy_earth.rune_blank": "空白符文",
    "item.fantasy_earth.rune_power": "力之符文",
    "item.fantasy_earth.rune_guard": "护之符文",
    "item.fantasy_earth.rune_flow": "流之符文",
    "item.fantasy_earth.rune_bind": "缚之符文",
    "item.fantasy_earth.rune_jade": "玉质符文",
    "item.fantasy_earth.jade_focus_housing": "玉质法器壳",
    "item.fantasy_earth.inscribed_ritual_core": "铭刻仪式核心",
    "item.fantasy_earth.construct_core": "构装核心",
    "item.fantasy_earth.ritual_candle": "仪式蜡烛",
    "item.fantasy_earth.offering_bowl": "仪式供碗",
    "item.fantasy_earth.ritual_circle_marker": "仪式法阵标记",
    "item.fantasy_earth.arcane_focus": "奥术法器",
    "item.fantasy_earth.spell_scroll": "法术卷轴",
    "block.fantasy_earth.aether_battery": "以太电池",
    "block.fantasy_earth.rune_stone": "符文石",
    "block.fantasy_earth.ritual_candle": "仪式蜡烛",
    "block.fantasy_earth.offering_bowl": "仪式供碗",
    "block.fantasy_earth.ritual_circle_marker": "仪式法阵标记",
    "entity.fantasy_earth.contract_golem": "契约魔像",
    "tooltip.fantasy_earth.evocation_notes": "塑能学派文献：记载奥术飞弹的聚焦与释放回路。",
    "tooltip.fantasy_earth.evocation_notes.use": "按住右键研读：需先完成奥术研究三阶与符文组合；施放时法器需二阶力之符文。",
    "tooltip.fantasy_earth.illusion_notes": "幻术学派文献：记载折射以太制造镜像隐匿的回路。",
    "tooltip.fantasy_earth.illusion_notes.use": "按住右键研读：需先完成奥术研究四阶与符文组合；施放时法器需二阶流之符文。",
    "tooltip.fantasy_earth.translocation_notes": "传送学派文献：记载短距空间折叠与闪现回路。",
    "tooltip.fantasy_earth.translocation_notes.use": "按住右键研读：需先完成奥术研究五阶与符文组合；施放时法器需三阶流之符文。",
    "tooltip.fantasy_earth.arcane_research_notes": "魔法学通用研究札记：记录研究台推演出的魔法学科节点。",
    "tooltip.fantasy_earth.arcane_research_notes.use": "按住右键研读，或在研究台上抄写。",
    "tooltip.fantasy_earth.aether_shard": "从聚魔水晶簇采掘或粉碎水晶所得的以太结晶碎片。",
    "tooltip.fantasy_earth.aether_lens": "法器聚焦组件：汇聚魔力流并提供 +10% 聚焦加成。",
    "tooltip.fantasy_earth.aether_conduit": "以太导能管：在未安装我的地球时替代铜电缆传输以太能。",
    "tooltip.fantasy_earth.aether_battery": "以太储能方块：最高储存 240 AEU，可与聚魔水晶双向充放。",
    "tooltip.fantasy_earth.rune_stone": "浸润魔尘的坚固石材，可在符文刻台上切割为空白符文。",
    "tooltip.fantasy_earth.rune_blank": "未经铭刻的石质符文坯。",
    "tooltip.fantasy_earth.rune_power": "力之符文：每阶提升 +10% 施法强度；潜行右键消耗符文墨水与以太碎片升阶。",
    "tooltip.fantasy_earth.rune_guard": "护之符文：每阶提升 +12% 护盾强度；用于构装核心唤醒。",
    "tooltip.fantasy_earth.rune_flow": "流之符文：每阶降低 2% 传能损耗；支撑幻术与传送回路。",
    "tooltip.fantasy_earth.rune_bind": "缚之符文：每阶延长 20 ticks 控制时长。",
    "tooltip.fantasy_earth.rune_jade": "玉质符文：最高可升至 4 阶，承载温润稳定的魔力回路。",
    "tooltip.fantasy_earth.jade_focus_housing": "玉质法器壳：副手持有并潜行右键奥术法器安装，+100 耐久并降低 15% 以太扰动。",
    "tooltip.fantasy_earth.inscribed_ritual_core": "铭刻了力与流回路的仪式核心，用于引导高阶仪式与构装唤醒。",
    "tooltip.fantasy_earth.construct_core": "构装核心：满能（240 AEU）且学会构装节点后，对地面使用召唤契约魔像。",
    "tooltip.fantasy_earth.construct_core.charge": "核心充能：%s / %s AEU",
    "tooltip.fantasy_earth.ritual_candle": "仪式蜡烛：右键点燃或熄灭，每支点燃蜡烛提升 +5% 仪式稳定度（最多计 4 支）。",
    "tooltip.fantasy_earth.offering_bowl": "仪式供碗：右键放入或取出仪式供品。",
    "tooltip.fantasy_earth.ritual_circle_marker": "仪式法阵标记：放置于仪式基座周围环圈以构成完整法阵，右键可收回。",
    "tooltip.fantasy_earth.arcane_focus": "奥术法器：右键施放当前法术；副手持有符文/宝石/玉壳时潜行右键镶嵌。",
    "tooltip.fantasy_earth.arcane_focus.durability": "法器耐久：%s / %s",
    "tooltip.fantasy_earth.arcane_focus.runes": "已镶嵌符文：%s",
    "tooltip.fantasy_earth.spell_scroll": "法术卷轴：潜行右键记录当前选中术式；右键免法器直接施放并消耗 1 张。",
    "tooltip.fantasy_earth.spell_scroll.graph": "记录术式：%s",
    "tooltip.fantasy_earth.aether_crystal.charge": "以太充能：%s / %s AEU",
    "tooltip.fantasy_earth.aether_crystal.signature": "场源签名：%s",
    "tooltip.fantasy_earth.rune.tier": "符文等阶：T%s",
    "tooltip.fantasy_earth.rune.grade": "玉石品质：%s",
    "message.fantasy_earth.aether_crystal.empty": "聚魔水晶充能不足，无法转化为法力。",
    "message.fantasy_earth.aether_crystal.converted": "聚魔水晶释放以太，恢复法力 +%s（剩余 %s/%s AEU）。",
    "message.fantasy_earth.aether_crystal.full": "聚魔水晶已满载，过量汲取引发了额外以太扰动。",
    "message.fantasy_earth.aether_crystal.no_field": "当前区块可提取以太场不足。",
    "message.fantasy_earth.aether_crystal.charged": "聚魔水晶汲取本地以太，当前充能 %s/%s AEU。",
    "message.fantasy_earth.aether_battery.status": "以太电池储量：%s / %s AEU",
    "message.fantasy_earth.rune.upgraded": "符文已升阶至 T%s！",
    "message.fantasy_earth.focus.rune_slotted": "已将符文 [%s] 镶入奥术法器。",
    "message.fantasy_earth.focus.jade_installed": "已为奥术法器加装玉质法器壳，耐久上限提升至 600。",
    "message.fantasy_earth.focus.duplicate_gem": "法器中已镶嵌相同品种的宝石（duplicate_gem）。",
    "message.fantasy_earth.focus.gem_slotted": "已将宝石 [%s] 镶入奥术法器。",
    "message.fantasy_earth.focus.broken": "奥术法器耐久已耗尽。",
    "message.fantasy_earth.spell_scroll.inscribed": "已在法术卷轴上铭刻术式：%s",
    "message.fantasy_earth.spell_scroll.empty_graph": "空白法术卷轴：请先潜行右键铭刻当前术式。",
    "message.fantasy_earth.construct_core.lore_locked": "你尚未掌握「构装」（K-MAG-21），无法唤醒契约魔像。",
    "message.fantasy_earth.construct_core.no_charge": "构装核心能量不足（需 240 AEU）。",
    "message.fantasy_earth.construct_core.no_space": "目标位置上方不足 2x3x2 净空，无法召唤契约魔像。",
    "message.fantasy_earth.construct_core.limit_reached": "你已有一只正在活动的契约魔像（上限 1 只）。",
    "message.fantasy_earth.construct_core.summoned": "构装核心轰鸣着展开，契约魔像已响应召唤！",
    "message.fantasy_earth.skill.rune_tier_low": "所持奥术法器中的符文等阶不足，无法驱动该学派术式。",
    "message.fantasy_earth.skill.missing_focus": "施放该学派法术需要主手或副手持有奥术法器。",
    "message.fantasy_earth.skill.activated.evocation": "奥术飞弹呼啸而出，消耗 %s 法力轰击目标。",
    "message.fantasy_earth.skill.activated.illusion": "镜像折光展开，消耗 %s 法力隐匿身形。",
    "message.fantasy_earth.skill.activated.translocation": "空间回路折叠，消耗 %s 法力完成短距闪现。",
    "screen.fantasy_earth.machine.too_cold": "以太场温度不足（需 %s K）",
    "screen.fantasy_earth.machine.aether_low": "以太供能不足（aether_low）",
    "screen.fantasy_earth.machine.lore_locked": "机主尚未掌握该配方所需的魔法学识",
    "screen.fantasy_earth.attunement.focus.evocation": "塑能",
    "screen.fantasy_earth.attunement.focus.evocation.desc": "凝练奥术飞弹投射伤害，施放需二阶力之符文。",
    "screen.fantasy_earth.attunement.focus.illusion": "幻术",
    "screen.fantasy_earth.attunement.focus.illusion.desc": "制造折光镜像与隐匿，施放需二阶流之符文。",
    "screen.fantasy_earth.attunement.focus.translocation": "传送",
    "screen.fantasy_earth.attunement.focus.translocation.desc": "沿视线方向短距闪现，施放需三阶流之符文。",
    "screen.fantasy_earth.attunement.skill.evocation.tooltip": "奥术飞弹：消耗法力轰击视线前方目标。",
    "screen.fantasy_earth.attunement.skill.illusion.tooltip": "镜像术：消耗法力进入短暂隐身与疾行。",
    "screen.fantasy_earth.attunement.skill.translocation.tooltip": "短距闪现：消耗法力向前方至多 8 格瞬移。",
    "recipe.fantasy_earth.machine.mana_salt_from_shard": "魔尘与以太碎片在 600 K 下结晶为魔盐",
    "recipe.fantasy_earth.machine.aether_crystal_synthesis": "以太碎片与魔尘在 800 K 下合成为聚魔水晶",
    "recipe.fantasy_earth.machine.aether_battery_synthesis": "聚魔水晶与符文铜片在炼金台中组装为以太电池",
    "recipe.fantasy_earth.machine.rune_blank": "在符文石上刻制空白符文坯",
    "recipe.fantasy_earth.machine.rune_power": "以荧石粉铭刻力之符文",
    "recipe.fantasy_earth.machine.rune_guard": "以铁锭铭刻护之符文",
    "recipe.fantasy_earth.machine.rune_flow": "以铜锭铭刻流之符文",
    "recipe.fantasy_earth.machine.rune_bind": "以丝线铭刻缚之符文",
    "recipe.fantasy_earth.machine.rune_jade": "在硬玉或绿宝石上铭刻玉质符文",
    "recipe.fantasy_earth.machine.dormant_ritual_core": "以仪式粉笔与符文墨水雕刻休眠仪式核心",
    "recipe.fantasy_earth.machine.aether_infusion": "仪式充能：为聚魔水晶注入满额以太",
    "recipe.fantasy_earth.machine.crystal_growth": "仪式培育：将以太碎片生长为聚魔水晶",
    "recipe.fantasy_earth.machine.construct_awakening": "构装唤醒：在成形仪式上将铭刻核心与护之符文铸为构装核心"
}

en_updates = {
    "itemGroup.fantasy_earth": "Earth: Magic",
    "key.categories.fantasy_earth.controls": "Earth: Magic",
    "guide.fantasy_earth.line0": "[Earth: Magic] Discipline build loaded",
    "screen.fantasy_earth.handbook.title": "Earth: Magic Handbook",
    "item.fantasy_earth.evocation_notes": "Evocation Arcane Notes",
    "item.fantasy_earth.illusion_notes": "Illusion Arcane Notes",
    "item.fantasy_earth.translocation_notes": "Translocation Arcane Notes",
    "item.fantasy_earth.arcane_research_notes": "Arcane Research Notes",
    "item.fantasy_earth.aether_shard": "Aether Shard",
    "item.fantasy_earth.aether_lens": "Aether Lens",
    "item.fantasy_earth.aether_conduit": "Aether Conduit",
    "item.fantasy_earth.aether_battery": "Aether Battery",
    "item.fantasy_earth.rune_stone": "Rune Stone",
    "item.fantasy_earth.rune_blank": "Blank Rune",
    "item.fantasy_earth.rune_power": "Power Rune",
    "item.fantasy_earth.rune_guard": "Guard Rune",
    "item.fantasy_earth.rune_flow": "Flow Rune",
    "item.fantasy_earth.rune_bind": "Bind Rune",
    "item.fantasy_earth.rune_jade": "Jade Rune",
    "item.fantasy_earth.jade_focus_housing": "Jade Focus Housing",
    "item.fantasy_earth.inscribed_ritual_core": "Inscribed Ritual Core",
    "item.fantasy_earth.construct_core": "Construct Core",
    "item.fantasy_earth.ritual_candle": "Ritual Candle",
    "item.fantasy_earth.offering_bowl": "Offering Bowl",
    "item.fantasy_earth.ritual_circle_marker": "Ritual Circle Marker",
    "item.fantasy_earth.arcane_focus": "Arcane Focus",
    "item.fantasy_earth.spell_scroll": "Spell Scroll",
    "block.fantasy_earth.aether_battery": "Aether Battery",
    "block.fantasy_earth.rune_stone": "Rune Stone",
    "block.fantasy_earth.ritual_candle": "Ritual Candle",
    "block.fantasy_earth.offering_bowl": "Offering Bowl",
    "block.fantasy_earth.ritual_circle_marker": "Ritual Circle Marker",
    "entity.fantasy_earth.contract_golem": "Contract Golem",
    "tooltip.fantasy_earth.evocation_notes": "Evocation literature: documents Arcane Missile projection circuits.",
    "tooltip.fantasy_earth.evocation_notes.use": "Hold right-click to study: requires Arcane Research III and Rune Combination; casting requires a tier 2+ Power Rune.",
    "tooltip.fantasy_earth.illusion_notes": "Illusion literature: documents aether-refraction mirror circuits.",
    "tooltip.fantasy_earth.illusion_notes.use": "Hold right-click to study: requires Arcane Research IV and Rune Combination; casting requires a tier 2+ Flow Rune.",
    "tooltip.fantasy_earth.translocation_notes": "Translocation literature: documents short-range spatial blink circuits.",
    "tooltip.fantasy_earth.translocation_notes.use": "Hold right-click to study: requires Arcane Research V and Rune Combination; casting requires a tier 3+ Flow Rune.",
    "tooltip.fantasy_earth.arcane_research_notes": "General magic research notes written at the Research Table.",
    "tooltip.fantasy_earth.arcane_research_notes.use": "Hold right-click to study, or copy at a Research Table.",
    "tooltip.fantasy_earth.aether_shard": "Crystalline shard harvested from Aether Crystal Clusters.",
    "tooltip.fantasy_earth.aether_lens": "Focus component: concentrates mana flow for a +10% focus bonus.",
    "tooltip.fantasy_earth.aether_conduit": "Aether energy conduit used when Earth on Minecraft is not installed.",
    "tooltip.fantasy_earth.aether_battery": "Aether storage block: holds up to 240 AEU and exchanges charge with Aether Crystals.",
    "tooltip.fantasy_earth.rune_stone": "Arcane-infused stone that can be cut into Blank Runes.",
    "tooltip.fantasy_earth.rune_blank": "Uninscribed stone rune blank.",
    "tooltip.fantasy_earth.rune_power": "Power Rune: +10% spell power per tier; sneak + right-click with rune ink and shard to upgrade.",
    "tooltip.fantasy_earth.rune_guard": "Guard Rune: +12% ward strength per tier; used in construct awakening.",
    "tooltip.fantasy_earth.rune_flow": "Flow Rune: -2% conduction loss per tier; required for Illusion and Translocation.",
    "tooltip.fantasy_earth.rune_bind": "Bind Rune: +20 ticks control duration per tier.",
    "tooltip.fantasy_earth.rune_jade": "Jade Rune: upgrades up to tier 4 with resonant jade stability.",
    "tooltip.fantasy_earth.jade_focus_housing": "Jade Focus Housing: hold in offhand and sneak + right-click an Arcane Focus to install (+100 durability, -15% disturbance).",
    "tooltip.fantasy_earth.inscribed_ritual_core": "Ritual core inscribed with Power and Flow circuits for advanced rituals.",
    "tooltip.fantasy_earth.construct_core": "Construct Core: when charged (240 AEU) and Constructs is learned, use on ground to summon a Contract Golem.",
    "tooltip.fantasy_earth.construct_core.charge": "Core Charge: %s / %s AEU",
    "tooltip.fantasy_earth.ritual_candle": "Ritual Candle: right-click to light/extinguish; +5% ritual stability per lit candle (up to 4).",
    "tooltip.fantasy_earth.offering_bowl": "Offering Bowl: right-click to insert or retrieve ritual offerings.",
    "tooltip.fantasy_earth.ritual_circle_marker": "Ritual Circle Marker: place around a Ritual Pedestal to form a ritual circle; right-click to pick up.",
    "tooltip.fantasy_earth.arcane_focus": "Arcane Focus: right-click to cast the active spell; sneak + right-click with offhand rune/gem/jade housing to install.",
    "tooltip.fantasy_earth.arcane_focus.durability": "Durability: %s / %s",
    "tooltip.fantasy_earth.arcane_focus.runes": "Slotted Runes: %s",
    "tooltip.fantasy_earth.spell_scroll": "Spell Scroll: sneak + right-click to inscribe active spell; right-click to cast without a focus.",
    "tooltip.fantasy_earth.spell_scroll.graph": "Inscribed Spell: %s",
    "tooltip.fantasy_earth.aether_crystal.charge": "Aether Charge: %s / %s AEU",
    "tooltip.fantasy_earth.aether_crystal.signature": "Field Signature: %s",
    "tooltip.fantasy_earth.rune.tier": "Rune Tier: T%s",
    "tooltip.fantasy_earth.rune.grade": "Jade Grade: %s",
    "message.fantasy_earth.aether_crystal.empty": "Not enough charge in the Aether Crystal to convert into mana.",
    "message.fantasy_earth.aether_crystal.converted": "The Aether Crystal releases energy, restoring +%s mana (%s/%s AEU remaining).",
    "message.fantasy_earth.aether_crystal.full": "The Aether Crystal is already full; overcharging disturbed the local field.",
    "message.fantasy_earth.aether_crystal.no_field": "The local extractable aether field is too depleted.",
    "message.fantasy_earth.aether_crystal.charged": "The Aether Crystal draws local aether: %s/%s AEU.",
    "message.fantasy_earth.aether_battery.status": "Aether Battery Charge: %s / %s AEU",
    "message.fantasy_earth.rune.upgraded": "Rune upgraded to T%s!",
    "message.fantasy_earth.focus.rune_slotted": "Slotted rune [%s] into the Arcane Focus.",
    "message.fantasy_earth.focus.jade_installed": "Installed Jade Focus Housing (+100 max durability).",
    "message.fantasy_earth.focus.duplicate_gem": "A gem of that species is already socketed (duplicate_gem).",
    "message.fantasy_earth.focus.gem_slotted": "Socketed gem [%s] into the Arcane Focus.",
    "message.fantasy_earth.focus.broken": "The Arcane Focus has no durability remaining.",
    "message.fantasy_earth.spell_scroll.inscribed": "Inscribed spell graph onto scroll: %s",
    "message.fantasy_earth.spell_scroll.empty_graph": "Blank spell scroll: sneak + right-click to inscribe your selected spell first.",
    "message.fantasy_earth.construct_core.lore_locked": "You have not learned Constructs (K-MAG-21) yet.",
    "message.fantasy_earth.construct_core.no_charge": "The Construct Core is not fully charged (requires 240 AEU).",
    "message.fantasy_earth.construct_core.no_space": "Requires a clear 2x3x2 space above the target block to summon a Contract Golem.",
    "message.fantasy_earth.construct_core.limit_reached": "You already have an active Contract Golem (limit 1).",
    "message.fantasy_earth.construct_core.summoned": "The Construct Core awakens a Contract Golem!",
    "message.fantasy_earth.skill.rune_tier_low": "Your Arcane Focus lacks the required rune tier for this school.",
    "message.fantasy_earth.skill.missing_focus": "This spell requires an Arcane Focus in your main hand or offhand.",
    "message.fantasy_earth.skill.activated.evocation": "Arcane Missiles lance forward, spending %s mana.",
    "message.fantasy_earth.skill.activated.illusion": "Mirror images refract around you, spending %s mana.",
    "message.fantasy_earth.skill.activated.translocation": "Space folds along your sightline, spending %s mana to blink.",
    "screen.fantasy_earth.machine.too_cold": "Field temperature too low (requires %s K)",
    "screen.fantasy_earth.machine.aether_low": "Insufficient local aether power (aether_low)",
    "screen.fantasy_earth.machine.lore_locked": "Owner lacks required Magic knowledge",
    "screen.fantasy_earth.attunement.focus.evocation": "Evocation",
    "screen.fantasy_earth.attunement.focus.evocation.desc": "Projects Arcane Missiles; casting requires a tier 2+ Power Rune.",
    "screen.fantasy_earth.attunement.focus.illusion": "Illusion",
    "screen.fantasy_earth.attunement.focus.illusion.desc": "Weaves mirror images and stealth; casting requires a tier 2+ Flow Rune.",
    "screen.fantasy_earth.attunement.focus.translocation": "Translocation",
    "screen.fantasy_earth.attunement.focus.translocation.desc": "Blinks up to 8 blocks along sightline; casting requires a tier 3+ Flow Rune.",
    "screen.fantasy_earth.attunement.skill.evocation.tooltip": "Arcane Missile: spends mana to strike targets ahead.",
    "screen.fantasy_earth.attunement.skill.illusion.tooltip": "Mirror Image: spends mana for brief invisibility and speed.",
    "screen.fantasy_earth.attunement.skill.translocation.tooltip": "Blink: spends mana to teleport up to 8 blocks forward.",
    "recipe.fantasy_earth.machine.mana_salt_from_shard": "Arcane dust and an aether shard crystallize into mana salt at 600 K",
    "recipe.fantasy_earth.machine.aether_crystal_synthesis": "Aether shards and arcane dust fuse into an Aether Crystal at 800 K",
    "recipe.fantasy_earth.machine.aether_battery_synthesis": "Aether crystals and rune copper plates assemble into an Aether Battery",
    "recipe.fantasy_earth.machine.rune_blank": "Cut a Rune Stone into Blank Runes",
    "recipe.fantasy_earth.machine.rune_power": "Inscribe a Power Rune with glowstone dust",
    "recipe.fantasy_earth.machine.rune_guard": "Inscribe a Guard Rune with an iron ingot",
    "recipe.fantasy_earth.machine.rune_flow": "Inscribe a Flow Rune with a copper ingot",
    "recipe.fantasy_earth.machine.rune_bind": "Inscribe a Bind Rune with string",
    "recipe.fantasy_earth.machine.rune_jade": "Inscribe a Jade Rune onto jadeite or emerald",
    "recipe.fantasy_earth.machine.dormant_ritual_core": "Carve a Dormant Ritual Core from chalk and rune ink",
    "recipe.fantasy_earth.machine.aether_infusion": "Ritual infusion: charge an Aether Crystal to full capacity",
    "recipe.fantasy_earth.machine.crystal_growth": "Ritual growth: grow Aether Shards into Aether Crystals",
    "recipe.fantasy_earth.machine.construct_awakening": "Construct Awakening: forge an Inscribed Core and Guard Runes into a Construct Core"
}

for n in NODES:
    s = n["slug"]
    zh_updates[f"lore.fantasy_earth.mag.{s}"] = n["zh"]
    zh_updates[f"lore.fantasy_earth.mag.{s}.desc"] = n["zh_desc"]
    en_updates[f"lore.fantasy_earth.mag.{s}"] = n["en"]
    en_updates[f"lore.fantasy_earth.mag.{s}.desc"] = n["en_desc"]

zh.update(zh_updates)
en.update(en_updates)
write_json(zh_path, zh)
write_json(en_path, en)
print(f"Generated {len(NODES)} MAG nodes, {len(NEW_BLOCKS)} blocks, {len(NEW_ITEMS)} items, {len(RECIPES)} recipes, {len(zh)} zh keys, {len(en)} en keys.")
