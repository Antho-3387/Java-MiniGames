package demo.teste.moreless.dao;

import demo.teste.moreless.model.Produit;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:justeprix.db";

    public static void init() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS produits (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    nom        TEXT    NOT NULL,
                    prix       REAL    NOT NULL,
                    image_path TEXT,
                    categorie  TEXT
                )
            """);

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM produits");
            if (rs.next() && rs.getInt(1) == 0) {
                seed(conn);
            }

        } catch (SQLException e) {
            System.err.println("DatabaseManager.init() : " + e.getMessage());
        }
    }

    private static void seed(Connection conn) throws SQLException {
        String sql = "INSERT INTO produits (nom, prix, image_path, categorie) VALUES (?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            Object[][] data = {
                // Électronique
                {"iPhone 15 Pro",               1299.99, "img/products/iphone_15_pro.png",                  "Électronique"},
                {"iPhone 15",                    899.99, "img/products/iphone_15.png",                      "Électronique"},
                {"Samsung Galaxy S24 Ultra",    1349.99, "img/products/samsung_galaxy_s24_ultra.jpg",       "Électronique"},
                {"Samsung Galaxy S24",           849.99, "img/products/samsung_galaxy_s24.jpg",             "Électronique"},
                {"Google Pixel 8 Pro",          1099.99, "img/products/google_pixel_8_pro.jpg",             "Électronique"},
                {"OnePlus 12",                   899.99, "img/products/oneplus_12.png",                     "Électronique"},
                {"Xiaomi 14",                    799.99, "img/products/xiaomi_14.jpg",                      "Électronique"},
                {"iPad Pro 12.9\"",             1219.99, "img/products/ipad_pro_12_9.jpg",                  "Électronique"},
                {"iPad Air M2",                  799.99, "img/products/ipad_air_m2.png",                    "Électronique"},
                {"Samsung Galaxy Tab S9+",      1049.99, "img/products/samsung_galaxy_tab_s9.jpg",          "Électronique"},
                {"Apple Watch Series 9",         449.99, "img/products/apple_watch_series_9.png",           "Électronique"},
                {"Samsung Galaxy Watch 6",       299.99, "img/products/samsung_galaxy_watch_6.png",         "Électronique"},
                {"Fitbit Charge 6",              159.99, "img/products/fitbit_charge_6.jpg",                "Électronique"},
                {"Samsung Galaxy Z Fold 5",     1799.99, "img/products/samsung_galaxy_z_fold_5.jpg",        "Électronique"},
                {"Sony Xperia 1 V",             1399.99, "img/products/sony_xperia_1_v.jpg",                "Électronique"},
                {"Nothing Phone 2",              649.99, "img/products/nothing_phone_2.jpg",                "Électronique"},
                {"Motorola Edge 40 Pro",         699.99, "img/products/motorola_edge_40_pro.jpg",           "Électronique"},
                {"Huawei P60 Pro",               999.99, "img/products/huawei_p60_pro.jpg",                 "Électronique"},
                {"Google Pixel 8",               749.99, "img/products/google_pixel_8.jpg",                 "Électronique"},
                {"iPhone 14",                    699.99, "img/products/iphone_14.jpg",                      "Électronique"},
                // Informatique
                {"MacBook Air M2 13\"",         1299.00, "",                                                 "Informatique"},
                {"MacBook Pro M3 14\"",         2199.00, "img/products/macbook_pro_m3_14.jpg",              "Informatique"},
                {"Dell XPS 15",                 1899.99, "img/products/dell_xps_15.jpg",                    "Informatique"},
                {"Lenovo ThinkPad X1 Carbon",   1599.99, "img/products/lenovo_thinkpad_x1_carbon.jpg",     "Informatique"},
                {"HP Spectre x360",             1499.99, "img/products/hp_spectre_x360.jpg",                "Informatique"},
                {"ASUS ZenBook 14",              999.99, "img/products/asus_zenbook_14.jpg",                "Informatique"},
                {"Microsoft Surface Laptop 5",  1299.99, "img/products/microsoft_surface_laptop_5.jpg",    "Informatique"},
                {"Logitech MX Master 3S",        109.99, "img/products/logitech_mx_master_3s.jpg",         "Informatique"},
                {"Clavier Keychron K2",           99.99, "img/products/clavier_keychron_k2.jpg",           "Informatique"},
                {"Écran Dell 27\" 4K",           449.99, "img/products/ecran_dell_27_4k.jpg",              "Informatique"},
                {"Écran LG 32\" UltraWide",      399.99, "img/products/ecran_lg_32_ultrawide.jpg",         "Informatique"},
                {"SSD Samsung 870 EVO 1 To",      89.99, "img/products/ssd_samsung_870_evo_1_to.jpg",      "Informatique"},
                {"SSD WD Black SN850X 2 To",     149.99, "img/products/ssd_wd_black_sn850x_2_to.jpg",      "Informatique"},
                {"RAM Corsair 32 GB DDR5",        129.99, "img/products/ram_corsair_32_gb_ddr5.jpg",       "Informatique"},
                {"GPU RTX 4070 Ti",              799.99, "img/products/gpu_rtx_4070_ti.jpg",               "Informatique"},
                // Gaming
                {"PlayStation 5",                549.99, "img/products/playstation_5.jpg",                 "Gaming"},
                {"Xbox Series X",                549.99, "img/products/xbox_series_x.jpg",                 "Gaming"},
                {"Nintendo Switch OLED",         349.99, "img/products/nintendo_switch_oled.jpg",          "Gaming"},
                {"Steam Deck 512 GB",            549.00, "img/products/steam_deck_512_gb.jpg",             "Gaming"},
                {"Manette PS5 DualSense",         74.99, "img/products/manette_ps5_dualsense.jpg",         "Gaming"},
                {"Casque PlayStation Pulse 3D",   99.99, "img/products/casque_ps_pulse_3d.png",            "Gaming"},
                {"Xbox Elite Controller Series 2",179.99, "img/products/xbox_elite_controller_2.jpg",      "Gaming"},
                {"Nintendo Pro Controller",        69.99, "img/products/nintendo_pro_controller.jpg",      "Gaming"},
                {"EA Sports FC 25 PS5",            69.99, "img/products/ea_sports_fc_25.jpg",              "Gaming"},
                {"Spider-Man 2 PS5",               79.99, "img/products/spider_man_2_ps5.png",             "Gaming"},
                {"Zelda Tears of the Kingdom",     59.99, "img/products/zelda_tears_of_the_kingdom.jpg",   "Gaming"},
                {"Hogwarts Legacy PS5",            69.99, "img/products/hogwarts_legacy.jpg",              "Gaming"},
                {"Elden Ring",                     49.99, "img/products/elden_ring.jpg",                   "Gaming"},
                {"Razer DeathAdder V3",            99.99, "img/products/razer_deathadder_v3.jpg",          "Gaming"},
                {"SSD Seagate pour PS5 2 To",     179.99, "img/products/ssd_seagate_ps5_2_to.jpg",        "Gaming"},
                // Audio
                {"AirPods Pro 2e gen",            279.00, "img/products/airpods_pro_2e_gen.jpg",           "Audio"},
                {"Sony WH-1000XM5",               349.99, "img/products/sony_wh_1000xm5.jpg",             "Audio"},
                {"Bose QuietComfort 45",           329.99, "",                                              "Audio"},
                {"Jabra Evolve2 85",               449.99, "img/products/jabra_evolve2_85.png",            "Audio"},
                {"Samsung Galaxy Buds2 Pro",       229.99, "img/products/samsung_galaxy_buds2_pro.jpg",    "Audio"},
                {"Sennheiser Momentum 4",          349.99, "img/products/sennheiser_momentum_4.jpg",       "Audio"},
                {"JBL Flip 6",                     129.99, "img/products/jbl_flip_6.jpg",                  "Audio"},
                {"Sonos Era 300",                  499.00, "img/products/sonos_era_300.jpg",               "Audio"},
                {"Bang & Olufsen Beosound A1",     249.00, "img/products/bang_olufsen_beosound_a1.jpg",    "Audio"},
                {"Devialet Phantom II",           1490.00, "img/products/devialet_phantom_ii.png",         "Audio"},
                // Photo & Vidéo
                {"GoPro HERO12 Black",             449.99, "img/products/gopro_hero12_black.jpg",          "Photo/Vidéo"},
                {"DJI Mini 4 Pro",                 799.00, "img/products/dji_mini_4_pro.jpg",              "Photo/Vidéo"},
                {"Sony Alpha 7 IV",               2799.99, "img/products/sony_alpha_7_iv.jpg",             "Photo/Vidéo"},
                {"Canon EOS R6 Mark II",          2699.99, "img/products/canon_eos_r6_mark_ii.jpg",        "Photo/Vidéo"},
                {"Nikon Z6 III",                  2799.99, "img/products/nikon_z6_iii.jpg",                "Photo/Vidéo"},
                {"Fujifilm X-T5",                 1799.99, "img/products/fujifilm_x_t5.jpg",               "Photo/Vidéo"},
                {"DJI Osmo Pocket 3",              379.00, "img/products/dji_osmo_pocket_3.jpg",           "Photo/Vidéo"},
                {"Insta360 X4",                    499.99, "img/products/insta360_x4.jpg",                 "Photo/Vidéo"},
                {"Sony ZV-E10",                    699.99, "img/products/sony_zv_e10.jpg",                 "Photo/Vidéo"},
                {"Objectif Sony 50mm f/1.8",       249.99, "img/products/objectif_sony_50mm_f1_8.jpg",    "Photo/Vidéo"},
                // Électroménager
                {"Cafetière Nespresso Vertuo Pop", 109.99, "img/products/nespresso_vertuo_pop.jpg",        "Électroménager"},
                {"Robot Aspirateur Roomba j7+",    699.00, "img/products/roomba_j7.jpg",                   "Électroménager"},
                {"Thermomix TM6",                 1459.00, "img/products/thermomix_tm6.jpg",               "Électroménager"},
                {"Aspirateur Dyson V15 Detect",    749.99, "img/products/dyson_v15_detect.jpg",            "Électroménager"},
                {"Air Fryer Philips XXL",           199.99, "img/products/air_fryer_philips_xxl.jpg",      "Électroménager"},
                {"Cafetière De'Longhi Magnifica",   699.99, "img/products/delonghi_magnifica.jpg",         "Électroménager"},
                {"Lave-vaisselle Bosch Serie 6",    799.99, "img/products/lave_vaisselle_bosch_serie_6.jpg","Électroménager"},
                {"Micro-ondes Samsung 32 L",        199.99, "img/products/micro_ondes_samsung_32_l.jpg",   "Électroménager"},
                {"Grille-pain Smeg 2 fentes",       169.99, "img/products/grille_pain_smeg.jpg",           "Électroménager"},
                {"Robot pâtissier KitchenAid",      699.00, "img/products/robot_kitchenaid.jpg",           "Électroménager"},
                // Vêtements
                {"Veste The North Face Resolve",    149.99, "img/products/the_north_face_resolve.jpg",     "Vêtements"},
                {"Doudoune Patagonia Down Sweater", 329.00, "img/products/patagonia_down_sweater.jpg",     "Vêtements"},
                {"Jean Levi's 501 Original",        109.95, "img/products/jean_levis_501.jpg",             "Vêtements"},
                {"Pull Ralph Lauren Classic",       149.99, "img/products/pull_ralph_lauren.jpg",          "Vêtements"},
                {"Manteau Lacoste Slim Fit",        299.99, "img/products/manteau_lacoste.jpg",            "Vêtements"},
                {"T-shirt Uniqlo Supima Cotton",     19.90, "img/products/t_shirt_uniqlo_supima.jpg",      "Vêtements"},
                {"Hoodie Champion Reverse Weave",    79.99, "",                                             "Vêtements"},
                {"Chemise Hugo Boss Regular",       129.99, "img/products/chemise_hugo_boss.jpg",          "Vêtements"},
                {"Short Gymshark Adapt",             44.99, "",                                             "Vêtements"},
                {"Legging Nike Pro",                 54.99, "img/products/legging_nike_pro.png",           "Vêtements"},
                // Chaussures
                {"Nike Air Max 270",                149.99, "img/products/nike_air_max_270.png",           "Chaussures"},
                {"Adidas Ultraboost 23",            189.99, "img/products/adidas_ultraboost_23.jpg",       "Chaussures"},
                {"New Balance 550",                 109.99, "img/products/new_balance_550.png",            "Chaussures"},
                {"Timberland 6-Inch Premium Boot",  219.99, "img/products/timberland_6_inch_boot.png",    "Chaussures"},
                {"Converse Chuck Taylor All Star",   69.99, "img/products/converse_chuck_taylor.jpg",      "Chaussures"},
                {"Nike Air Force 1",                109.99, "img/products/nike_air_force_1.jpg",           "Chaussures"},
                {"Vans Old Skool",                   84.99, "img/products/vans_old_skool.jpg",             "Chaussures"},
                {"Saucony Jazz Original",            89.99, "img/products/saucony_jazz_original.jpg",      "Chaussures"},
                // Montres
                {"Casio G-Shock GA-2100",           129.99, "img/products/casio_g_shock_ga_2100.jpg",     "Montres"},
                {"Timex Expedition Scout",           79.99, "img/products/timex_expedition_scout.jpg",    "Montres"},
                {"Seiko 5 Sports Automatique",      299.99, "img/products/seiko_5_sports.png",            "Montres"},
                {"Citizen Eco-Drive Promaster",     449.99, "img/products/citizen_eco_drive_promaster.jpg","Montres"},
                {"Fossil Gen 6 Smartwatch",         179.99, "img/products/fossil_gen_6_smartwatch.jpg",   "Montres"},
                // Sport & Fitness
                {"Vélo de route Btwin 900",         899.99, "img/products/velo_route_btwin_900.jpg",      "Sport"},
                {"Tapis de course NordicTrack",    1299.99, "img/products/tapis_course_nordictrack.png",  "Sport"},
                {"Kettlebell 16 kg Reebok",          49.99, "img/products/kettlebell_16_kg.jpg",          "Sport"},
                {"Corde à sauter CrossFit",          19.99, "img/products/corde_a_sauter_crossfit.jpg",   "Sport"},
                {"Casque vélo Giro Syntax MIPS",    149.99, "",                                            "Sport"},
                {"Raquette de tennis Wilson Pro",   179.99, "img/products/raquette_wilson_pro.jpg",       "Sport"},
                // Maison & Déco
                {"Purificateur Dyson HP07",         599.99, "img/products/dyson_hp07.png",                "Maison"},
                {"Lampe Xiaomi Mi LED",              34.99, "img/products/lampe_xiaomi_mi_led.jpg",       "Maison"},
                {"French Press Bodum 8 tasses",      29.99, "img/products/french_press_bodum.jpg",        "Maison"},
                {"Plaid Zara Home Sherpa",           49.99, "img/products/plaid_sherpa.jpg",              "Maison"},
                {"Cadre photo numérique Aura",      199.99, "img/products/cadre_photo_numerique_aura.jpg","Maison"},
            };
            for (Object[] row : data) {
                ps.setString(1, (String) row[0]);
                ps.setDouble(2, (Double) row[1]);
                ps.setString(3, (String) row[2]);
                ps.setString(4, (String) row[3]);
                ps.executeUpdate();
            }
        }
    }

    public static List<Produit> findAll() {
        List<Produit> list = new ArrayList<>();
        String sql = "SELECT id, nom, prix, image_path, categorie FROM produits ORDER BY id";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            System.err.println("DatabaseManager.findAll() : " + e.getMessage());
        }
        return list;
    }

    public static Produit findRandom() {
        String sql = "SELECT id, nom, prix, image_path, categorie FROM produits ORDER BY RANDOM() LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return map(rs);
        } catch (SQLException e) {
            System.err.println("DatabaseManager.findRandom() : " + e.getMessage());
        }
        return null;
    }

    public static void insert(Produit p) {
        String sql = "INSERT INTO produits (nom, prix, image_path, categorie) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setDouble(2, p.getPrix());
            ps.setString(3, p.getImagePath() != null ? p.getImagePath() : "");
            ps.setString(4, p.getCategorie());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DatabaseManager.insert() : " + e.getMessage());
        }
    }

    public static void update(Produit p) {
        String sql = "UPDATE produits SET nom=?, prix=?, image_path=?, categorie=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getNom());
            ps.setDouble(2, p.getPrix());
            ps.setString(3, p.getImagePath() != null ? p.getImagePath() : "");
            ps.setString(4, p.getCategorie());
            ps.setInt(5, p.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DatabaseManager.update() : " + e.getMessage());
        }
    }

    public static void delete(int id) {
        String sql = "DELETE FROM produits WHERE id=?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("DatabaseManager.delete() : " + e.getMessage());
        }
    }

    private static Produit map(ResultSet rs) throws SQLException {
        return new Produit(
            rs.getInt("id"),
            rs.getString("nom"),
            rs.getDouble("prix"),
            rs.getString("image_path"),
            rs.getString("categorie")
        );
    }
}