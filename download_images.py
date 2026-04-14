#!/usr/bin/env python3
"""
Télécharge les images produits via DuckDuckGo Images (pas de clé API requise).
Lance depuis la racine du projet (là où est pom.xml).

Usage :
    pip install requests
    python3 download_images.py
"""

import os
import re
import time
import json
import random
import urllib.parse
import requests

OUTPUT_DIR = "img/products"

PRODUCTS = [
    # Électronique
    ("iPhone 15 Pro",               "iphone_15_pro",               "iPhone 15 Pro smartphone Apple"),
    ("iPhone 15",                   "iphone_15",                   "iPhone 15 smartphone Apple"),
    ("Samsung Galaxy S24 Ultra",    "samsung_galaxy_s24_ultra",    "Samsung Galaxy S24 Ultra smartphone"),
    ("Samsung Galaxy S24",          "samsung_galaxy_s24",          "Samsung Galaxy S24 smartphone"),
    ("Google Pixel 8 Pro",          "google_pixel_8_pro",          "Google Pixel 8 Pro smartphone"),
    ("OnePlus 12",                  "oneplus_12",                  "OnePlus 12 smartphone"),
    ("Xiaomi 14",                   "xiaomi_14",                   "Xiaomi 14 smartphone"),
    ("iPad Pro 12.9",               "ipad_pro_12_9",               "Apple iPad Pro 12.9 tablet"),
    ("iPad Air M2",                 "ipad_air_m2",                 "Apple iPad Air M2 tablet"),
    ("Samsung Galaxy Tab S9+",      "samsung_galaxy_tab_s9",       "Samsung Galaxy Tab S9+ tablet"),
    ("Apple Watch Series 9",        "apple_watch_series_9",        "Apple Watch Series 9 smartwatch"),
    ("Samsung Galaxy Watch 6",      "samsung_galaxy_watch_6",      "Samsung Galaxy Watch 6"),
    ("Fitbit Charge 6",             "fitbit_charge_6",             "Fitbit Charge 6 fitness tracker"),
    ("Samsung Galaxy Z Fold 5",     "samsung_galaxy_z_fold_5",     "Samsung Galaxy Z Fold 5 foldable"),
    ("Sony Xperia 1 V",             "sony_xperia_1_v",             "Sony Xperia 1 V smartphone"),
    ("Nothing Phone 2",             "nothing_phone_2",             "Nothing Phone 2 smartphone"),
    ("Motorola Edge 40 Pro",        "motorola_edge_40_pro",        "Motorola Edge 40 Pro smartphone"),
    ("Huawei P60 Pro",              "huawei_p60_pro",              "Huawei P60 Pro smartphone"),
    ("Google Pixel 8",              "google_pixel_8",              "Google Pixel 8 smartphone"),
    ("iPhone 14",                   "iphone_14",                   "iPhone 14 Apple smartphone"),
    # Informatique
    ("MacBook Air M2 13",           "macbook_air_m2_13",           "MacBook Air M2 13 inch laptop"),
    ("MacBook Pro M3 14",           "macbook_pro_m3_14",           "MacBook Pro M3 14 inch laptop"),
    ("Dell XPS 15",                 "dell_xps_15",                 "Dell XPS 15 laptop"),
    ("Lenovo ThinkPad X1 Carbon",   "lenovo_thinkpad_x1_carbon",   "Lenovo ThinkPad X1 Carbon laptop"),
    ("HP Spectre x360",             "hp_spectre_x360",             "HP Spectre x360 laptop"),
    ("ASUS ZenBook 14",             "asus_zenbook_14",             "ASUS ZenBook 14 laptop"),
    ("Microsoft Surface Laptop 5",  "microsoft_surface_laptop_5",  "Microsoft Surface Laptop 5"),
    ("Logitech MX Master 3S",       "logitech_mx_master_3s",       "Logitech MX Master 3S mouse"),
    ("Clavier Keychron K2",         "clavier_keychron_k2",         "Keychron K2 mechanical keyboard"),
    ("Ecran Dell 27 4K",            "ecran_dell_27_4k",            "Dell 27 inch 4K monitor"),
    ("Ecran LG 32 UltraWide",       "ecran_lg_32_ultrawide",       "LG 32 inch UltraWide monitor"),
    ("SSD Samsung 870 EVO 1 To",    "ssd_samsung_870_evo_1_to",    "Samsung 870 EVO SSD 1TB"),
    ("SSD WD Black SN850X 2 To",    "ssd_wd_black_sn850x_2_to",    "WD Black SN850X SSD 2TB"),
    ("RAM Corsair 32 GB DDR5",      "ram_corsair_32_gb_ddr5",      "Corsair 32GB DDR5 RAM"),
    ("GPU RTX 4070 Ti",             "gpu_rtx_4070_ti",             "NVIDIA RTX 4070 Ti GPU graphics card"),
    # Gaming
    ("PlayStation 5",               "playstation_5",               "PlayStation 5 console PS5"),
    ("Xbox Series X",               "xbox_series_x",               "Xbox Series X console"),
    ("Nintendo Switch OLED",        "nintendo_switch_oled",        "Nintendo Switch OLED console"),
    ("Steam Deck 512 GB",           "steam_deck_512_gb",           "Steam Deck handheld gaming"),
    ("Manette PS5 DualSense",       "manette_ps5_dualsense",       "PS5 DualSense controller"),
    ("Casque PlayStation Pulse 3D", "casque_ps_pulse_3d",          "PlayStation Pulse 3D headset"),
    ("Xbox Elite Controller 2",     "xbox_elite_controller_2",     "Xbox Elite Controller Series 2"),
    ("Nintendo Pro Controller",     "nintendo_pro_controller",     "Nintendo Switch Pro Controller"),
    ("EA Sports FC 25",             "ea_sports_fc_25",             "EA Sports FC 25 game cover"),
    ("Spider-Man 2 PS5",            "spider_man_2_ps5",            "Spider-Man 2 PS5 game"),
    ("Zelda Tears of the Kingdom",  "zelda_tears_of_the_kingdom",  "Zelda Tears of the Kingdom game"),
    ("Hogwarts Legacy",             "hogwarts_legacy",             "Hogwarts Legacy game"),
    ("Elden Ring",                  "elden_ring",                  "Elden Ring game cover"),
    ("Razer DeathAdder V3",         "razer_deathadder_v3",         "Razer DeathAdder V3 gaming mouse"),
    ("SSD Seagate PS5 2 To",        "ssd_seagate_ps5_2_to",        "Seagate FireCuda 530 SSD PS5"),
    # Audio
    ("AirPods Pro 2e gen",          "airpods_pro_2e_gen",          "Apple AirPods Pro 2nd generation"),
    ("Sony WH-1000XM5",             "sony_wh_1000xm5",             "Sony WH-1000XM5 headphones"),
    ("Bose QuietComfort 45",        "bose_quietcomfort_45",        "Bose QuietComfort 45 headphones"),
    ("Jabra Evolve2 85",            "jabra_evolve2_85",            "Jabra Evolve2 85 headset"),
    ("Samsung Galaxy Buds2 Pro",    "samsung_galaxy_buds2_pro",    "Samsung Galaxy Buds2 Pro earbuds"),
    ("Sennheiser Momentum 4",       "sennheiser_momentum_4",       "Sennheiser Momentum 4 headphones"),
    ("JBL Flip 6",                  "jbl_flip_6",                  "JBL Flip 6 bluetooth speaker"),
    ("Sonos Era 300",               "sonos_era_300",               "Sonos Era 300 speaker"),
    ("Bang Olufsen Beosound A1",    "bang_olufsen_beosound_a1",    "Bang Olufsen Beosound A1 speaker"),
    ("Devialet Phantom II",         "devialet_phantom_ii",         "Devialet Phantom II speaker"),
    # Photo / Vidéo
    ("GoPro HERO12 Black",          "gopro_hero12_black",          "GoPro HERO12 Black camera"),
    ("DJI Mini 4 Pro",              "dji_mini_4_pro",              "DJI Mini 4 Pro drone"),
    ("Sony Alpha 7 IV",             "sony_alpha_7_iv",             "Sony Alpha 7 IV mirrorless camera"),
    ("Canon EOS R6 Mark II",        "canon_eos_r6_mark_ii",        "Canon EOS R6 Mark II camera"),
    ("Nikon Z6 III",                "nikon_z6_iii",                "Nikon Z6 III mirrorless camera"),
    ("Fujifilm X-T5",               "fujifilm_x_t5",               "Fujifilm X-T5 camera"),
    ("DJI Osmo Pocket 3",           "dji_osmo_pocket_3",           "DJI Osmo Pocket 3 camera"),
    ("Insta360 X4",                 "insta360_x4",                 "Insta360 X4 360 camera"),
    ("Sony ZV-E10",                 "sony_zv_e10",                 "Sony ZV-E10 vlog camera"),
    ("Objectif Sony 50mm f1.8",     "objectif_sony_50mm_f1_8",     "Sony FE 50mm f1.8 lens"),
    # Électroménager
    ("Nespresso Vertuo Pop",        "nespresso_vertuo_pop",        "Nespresso Vertuo Pop coffee machine"),
    ("Roomba j7+",                  "roomba_j7",                   "iRobot Roomba j7+ robot vacuum"),
    ("Thermomix TM6",               "thermomix_tm6",               "Thermomix TM6 kitchen robot"),
    ("Dyson V15 Detect",            "dyson_v15_detect",            "Dyson V15 Detect vacuum cleaner"),
    ("Air Fryer Philips XXL",       "air_fryer_philips_xxl",       "Philips Airfryer XXL"),
    ("DeLonghi Magnifica",          "delonghi_magnifica",          "DeLonghi Magnifica coffee machine"),
    ("Lave-vaisselle Bosch Serie 6","lave_vaisselle_bosch_serie_6","Bosch Serie 6 dishwasher"),
    ("Micro-ondes Samsung 32 L",    "micro_ondes_samsung_32_l",    "Samsung microwave 32L"),
    ("Grille-pain Smeg",            "grille_pain_smeg",            "Smeg toaster 2 slots"),
    ("Robot KitchenAid",            "robot_kitchenaid",            "KitchenAid Artisan stand mixer"),
    # Vêtements
    ("The North Face Resolve",      "the_north_face_resolve",      "The North Face Resolve jacket"),
    ("Patagonia Down Sweater",      "patagonia_down_sweater",      "Patagonia Down Sweater jacket"),
    ("Jean Levis 501",              "jean_levis_501",              "Levi's 501 Original jeans"),
    ("Pull Ralph Lauren",           "pull_ralph_lauren",           "Ralph Lauren Classic polo shirt"),
    ("Manteau Lacoste",             "manteau_lacoste",             "Lacoste Slim Fit coat"),
    ("T-shirt Uniqlo Supima",       "t_shirt_uniqlo_supima",       "Uniqlo Supima Cotton T-shirt"),
    ("Hoodie Champion",             "hoodie_champion",             "Champion Reverse Weave hoodie"),
    ("Chemise Hugo Boss",           "chemise_hugo_boss",           "Hugo Boss Regular shirt"),
    ("Short Gymshark Adapt",        "short_gymshark_adapt",        "Gymshark Adapt shorts"),
    ("Legging Nike Pro",            "legging_nike_pro",            "Nike Pro leggings"),
    # Chaussures
    ("Nike Air Max 270",            "nike_air_max_270",            "Nike Air Max 270 sneakers"),
    ("Adidas Ultraboost 23",        "adidas_ultraboost_23",        "Adidas Ultraboost 23 running shoes"),
    ("New Balance 550",             "new_balance_550",             "New Balance 550 sneakers"),
    ("Timberland 6-Inch Boot",      "timberland_6_inch_boot",      "Timberland 6-Inch Premium Boot"),
    ("Converse Chuck Taylor",       "converse_chuck_taylor",       "Converse Chuck Taylor All Star"),
    ("Nike Air Force 1",            "nike_air_force_1",            "Nike Air Force 1 sneakers"),
    ("Vans Old Skool",              "vans_old_skool",              "Vans Old Skool sneakers"),
    ("Saucony Jazz Original",       "saucony_jazz_original",       "Saucony Jazz Original sneakers"),
    # Montres
    ("Casio G-Shock GA-2100",       "casio_g_shock_ga_2100",       "Casio G-Shock GA-2100 watch"),
    ("Timex Expedition Scout",      "timex_expedition_scout",      "Timex Expedition Scout watch"),
    ("Seiko 5 Sports",              "seiko_5_sports",              "Seiko 5 Sports automatic watch"),
    ("Citizen Eco-Drive Promaster", "citizen_eco_drive_promaster", "Citizen Eco-Drive Promaster watch"),
    ("Fossil Gen 6 Smartwatch",     "fossil_gen_6_smartwatch",     "Fossil Gen 6 Smartwatch"),
    # Sport
    ("Velo route Btwin 900",        "velo_route_btwin_900",        "Btwin Triban 900 road bicycle"),
    ("Tapis course NordicTrack",    "tapis_course_nordictrack",    "NordicTrack treadmill"),
    ("Kettlebell 16 kg",            "kettlebell_16_kg",            "Reebok kettlebell 16kg"),
    ("Corde a sauter CrossFit",     "corde_a_sauter_crossfit",     "CrossFit jump rope"),
    ("Casque velo Giro MIPS",       "casque_velo_giro_mips",       "Giro Syntax MIPS bicycle helmet"),
    ("Raquette Wilson Pro",         "raquette_wilson_pro",         "Wilson Pro Staff tennis racket"),
    # Maison
    ("Dyson HP07",                  "dyson_hp07",                  "Dyson HP07 air purifier"),
    ("Lampe Xiaomi Mi LED",         "lampe_xiaomi_mi_led",         "Xiaomi Mi LED desk lamp"),
    ("French Press Bodum",          "french_press_bodum",          "Bodum French Press 8 cups"),
    ("Plaid Sherpa",                "plaid_sherpa",                "Sherpa blanket plaid"),
    ("Cadre photo numerique Aura",  "cadre_photo_numerique_aura",  "Aura digital photo frame"),
]


# ─── DuckDuckGo image search ──────────────────────────────────────────────────

SESSION = requests.Session()
SESSION.headers.update({
    "User-Agent": (
        "Mozilla/5.0 (X11; Linux x86_64) "
        "AppleWebKit/537.36 (KHTML, like Gecko) "
        "Chrome/122.0.0.0 Safari/537.36"
    ),
    "Accept-Language": "fr-FR,fr;q=0.9,en;q=0.8",
})

DDG_TOKEN_URL = "https://duckduckgo.com/"
DDG_IMAGES_URL = "https://duckduckgo.com/i.js"


def get_vqd(query: str) -> str | None:
    """Récupère le token vqd nécessaire à DuckDuckGo."""
    try:
        r = SESSION.get(DDG_TOKEN_URL, params={"q": query}, timeout=10)
        # Token dans la réponse HTML
        match = re.search(r'vqd=(["\'])([^"\']+)\1', r.text)
        if match:
            return match.group(2)
        # Fallback: autre format
        match = re.search(r"vqd='([^']+)'", r.text)
        if match:
            return match.group(1)
    except Exception as e:
        print(f"      token err: {e}")
    return None


def search_image(query: str) -> str | None:
    """Retourne l'URL de la première image trouvée sur DuckDuckGo."""
    vqd = get_vqd(query)
    if not vqd:
        return None

    params = {
        "l":    "fr-fr",
        "o":    "json",
        "q":    query,
        "vqd":  vqd,
        "f":    ",,,,,",
        "p":    "1",
    }
    try:
        r = SESSION.get(DDG_IMAGES_URL, params=params, timeout=10)
        data = r.json()
        results = data.get("results", [])
        if results:
            # Prend la première image avec une URL directe (jpg/png)
            for result in results[:5]:
                url = result.get("image", "")
                if url and any(url.lower().endswith(ext) for ext in [".jpg", ".jpeg", ".png", ".webp"]):
                    return url
            # Fallback: première URL quelle qu'elle soit
            return results[0].get("image")
    except Exception as e:
        print(f"      search err: {e}")
    return None


def download_image(url: str, dest: str) -> bool:
    """Télécharge une image depuis une URL vers dest."""
    try:
        r = SESSION.get(url, timeout=15, stream=True)
        r.raise_for_status()
        content_type = r.headers.get("Content-Type", "")
        if "image" not in content_type and not url.lower().endswith((".jpg", ".jpeg", ".png", ".webp")):
            return False
        with open(dest, "wb") as f:
            for chunk in r.iter_content(8192):
                f.write(chunk)
        # Vérifie que le fichier n'est pas vide ou trop petit (< 5 KB = probablement une erreur)
        if os.path.getsize(dest) < 5000:
            os.remove(dest)
            return False
        return True
    except Exception as e:
        print(f"      dl err: {e}")
        if os.path.exists(dest):
            os.remove(dest)
        return False


def ext_from_url(url: str) -> str:
    url_clean = url.split("?")[0].lower()
    if url_clean.endswith(".png"):
        return ".png"
    if url_clean.endswith(".webp"):
        return ".webp"
    return ".jpg"


# ─── Main ────────────────────────────────────────────────────────────────────

def main():
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    ok, fail = 0, 0
    failed = []

    total = len(PRODUCTS)
    for i, (nom, slug, query) in enumerate(PRODUCTS, 1):
        # Cherche les extensions possibles déjà téléchargées
        already = any(
            os.path.exists(os.path.join(OUTPUT_DIR, slug + ext))
            for ext in [".jpg", ".png", ".webp"]
        )
        if already:
            print(f"[{i:>3}/{total}]  ↷  {nom}")
            ok += 1
            continue

        print(f"[{i:>3}/{total}]  ↓  {nom} …", end=" ", flush=True)

        img_url = search_image(query)
        if not img_url:
            print("✗  (aucun résultat DDG)")
            fail += 1
            failed.append(nom)
            time.sleep(1)
            continue

        ext  = ext_from_url(img_url)
        dest = os.path.join(OUTPUT_DIR, slug + ext)

        if download_image(img_url, dest):
            print(f"OK  ({ext})")
            ok += 1
        else:
            print("✗  (téléchargement échoué)")
            fail += 1
            failed.append(nom)

        # Délai aléatoire pour ne pas se faire bannir (1.5 à 3.5 s)
        time.sleep(random.uniform(1.5, 3.5))

    print(f"\n{'─'*55}")
    print(f"  ✓ {ok} image(s)   ✗ {fail} échouée(s)")

    if failed:
        print("\nÀ récupérer manuellement :")
        for n in failed:
            print(f"  - {n}")

    # Affiche le mapping final pour DatabaseManager.java
    print("\n─── Noms de fichiers pour image_path ───")
    for nom, slug, _ in PRODUCTS:
        for ext in [".jpg", ".png", ".webp"]:
            path = os.path.join(OUTPUT_DIR, slug + ext)
            if os.path.exists(path):
                print(f'  // {nom}  →  "{OUTPUT_DIR}/{slug}{ext}"')
                break


if __name__ == "__main__":
    main()