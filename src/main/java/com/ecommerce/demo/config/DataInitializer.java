package com.ecommerce.demo.config;

import com.ecommerce.demo.entity.*;
import com.ecommerce.demo.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CouponRepository couponRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final PasswordEncoder passwordEncoder;

    private final Random random = new Random();

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) return; // 已初始化就跳過

        // ==================== 用戶 ====================
        List<User> users = new ArrayList<>();
        users.add(createUser("admin", "admin@demo.com", "123456", UserRole.ADMIN));
        users.add(createUser("buyer1", "buyer1@demo.com", "123456", UserRole.BUYER));
        users.add(createUser("buyer2", "buyer2@demo.com", "123456", UserRole.BUYER));
        users.add(createUser("buyer3", "buyer3@demo.com", "123456", UserRole.BUYER));
        users.add(createUser("visitor", "visitor@demo.com", "123456", UserRole.BUYER));
        userRepository.saveAll(users);
        System.out.println("✅ 5 個用戶已建立");

        // ==================== 標籤 ====================
        List<Tag> tags = new ArrayList<>();
        tags.add(createTag("電子產品", "#6366f1"));
        tags.add(createTag("家電", "#f59e0b"));
        tags.add(createTag("服飾", "#ec4899"));
        tags.add(createTag("生活用品", "#22c55e"));
        tags.add(createTag("食品", "#ef4444"));
        tags.add(createTag("運動戶外", "#06b6d4"));
        tags.add(createTag("書籍", "#8b5cf6"));
        tags.add(createTag("辦公用品", "#64748b"));
        tags.add(createTag("美妝保養", "#f43f5e"));
        tags.add(createTag("寵物用品", "#84cc16"));
        tags.add(createTag("汽車用品", "#f97316"));
        tags.add(createTag("園藝", "#14b8a6"));
        tags.add(createTag("熱賣", "#dc2626"));
        tags.add(createTag("新品", "#2563eb"));
        tags.add(createTag("限時優惠", "#ca8a04"));
        tagRepository.saveAll(tags);
        System.out.println("✅ " + tags.size() + " 個標籤已建立");

        // ==================== 50 個商品 ====================
        List<Product> products = new ArrayList<>();
        products.add(createProduct("iPhone 15 Pro Max 256GB", "A17 Pro 晶片，鈦金屬設計，4800 萬畫素主相機", 44900, 25, randomTags(tags, "電子產品", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/iPhone_15%20Pro_Max_256GB.png"));
        products.add(createProduct("Samsung Galaxy S24 Ultra", "Snapdragon 8 Gen 3，S Pen，200MP 相機", 40900, 20, randomTags(tags, "電子產品", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Samsung_Galaxy_S24_%20Ultra.jpg"));
        products.add(createProduct("MacBook Air 15 M3", "8GB RAM / 256GB SSD，Liquid Retina 顯示器", 42900, 15, randomTags(tags, "電子產品", "辦公用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/MacBook_Air_15_M3.jpg"));
        products.add(createProduct("iPad Air 11 M2", "Wi-Fi 128GB，支援 Apple Pencil Pro", 19900, 18, randomTags(tags, "電子產品", "新品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/iPad_Air_11_M2.png"));
        products.add(createProduct("Sony WH-1000XM5 降噪耳機", "業界最強降噪，30 小時續航", 11900, 30, randomTags(tags, "電子產品", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Sony_WH-1000XM5.png"));
        products.add(createProduct("Apple Watch Series 9", "GPS 45mm，血氧偵測，運動追蹤", 13900, 22, randomTags(tags, "電子產品", "運動戶外"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Apple_Watch_Series_9.jpg"));
        products.add(createProduct("Nintendo Switch OLED", "7 吋 OLED 螢幕，64GB，可拆式 Joy-Con", 10500, 28, randomTags(tags, "電子產品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Nintendo_Switch_OLED.jpg"));
        products.add(createProduct("Dyson V15 Detect 無線吸塵器", "雷射偵測微塵，LCD 螢幕，60 分鐘續航", 24900, 10, randomTags(tags, "家電", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Dyson_V15_Detect.webp"));
        products.add(createProduct("Dyson Pure Cool 空氣清淨機", "HEPA H13 濾網，Wi-Fi 連線，夜間模式", 19900, 8, randomTags(tags, "家電", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Dyson_Pure_Cool.jpg"));
        products.add(createProduct("Panasonic 42L 烤箱", "上下獨立溫控，發酵功能，旋轉烤架", 5990, 12, randomTags(tags, "家電"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Panasonic42L.jpg"));
        products.add(createProduct("象印 10 人份 IH 電子鍋", "IH 加熱，多種烹調模式，預約定時", 8990, 10, randomTags(tags, "家電", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/10IH.jpg"));
        products.add(createProduct("LG 15kg 滾筒洗衣機", "蒸氣殺菌，AI DD 智慧偵測，Wi-Fi 遠端", 32900, 6, randomTags(tags, "家電"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/LG15kg.jpg"));
        products.add(createProduct("BRUNO 多功能電烤盤", "章魚燒/烤肉/火鍋，不沾塗層", 3290, 35, randomTags(tags, "家電", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/BRUNO.jpg"));
        products.add(createProduct("Nike Air Force 1 '07", "經典白鞋，皮革鞋面，Air 氣墊", 3400, 40, randomTags(tags, "服飾", "運動戶外", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/NikeAirForce1%2707.png"));
        products.add(createProduct("Adidas 三線棉質長褲", "寬鬆版型，純棉材質，男女皆可", 2290, 50, randomTags(tags, "服飾", "運動戶外"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Adidas3.webp"));
        products.add(createProduct("Uniqlo 輕量羽絨外套", "輕薄保暖，附收納袋，抗潑水", 1990, 60, randomTags(tags, "服飾", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Uniqlo3.jpg"));
        products.add(createProduct("Levi's 501 經典直筒牛仔褲", "原色丹寧，經典剪裁", 3290, 25, randomTags(tags, "服飾", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Levi%27s501.jpg"));
        products.add(createProduct("MUJI 有機棉 T 恤", "100% 有機棉，圓領設計", 590, 100, randomTags(tags, "服飾", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/4550583097561_1260.jpg"));
        products.add(createProduct("SK-II 青春露 230ml", "PITERA™ 精華，保濕透亮", 4980, 30, randomTags(tags, "美妝保養", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/84402f687da9cc92727192d038b2dcb6.webp"));
        products.add(createProduct("資生堂 安耐曬 金鑽防曬", "SPF50+ PA++++，防水抗汗", 790, 55, randomTags(tags, "美妝保養", "限時優惠"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/2025062808391501_600x600.jpg"));
        products.add(createProduct("IKEA 書桌 MICKE", "白色 142x50cm，附收納抽屜", 2990, 20, randomTags(tags, "辦公用品", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/0736576_PE740630_S4.jpg"));
        products.add(createProduct("Herman Miller Aeron 人體工學椅", "全功能可調，前傾設計，12 年保固", 45000, 5, randomTags(tags, "辦公用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/_abc_herman_miller_aeron_2_off_1731180960_0e5fa1ec_progressive.jpg"));
        products.add(createProduct("3M 隨手黏黏把", "60 張補充包，除塵滾筒", 199, 80, randomTags(tags, "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/images.jpg"));
        products.add(createProduct("無印良品 超音波芬香噴霧器", "LED 燈，靜音設計，2 段定時", 1690, 25, randomTags(tags, "生活用品", "新品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/w360.jpg"));
        products.add(createProduct("樂扣樂扣 保鮮盒 12 件組", "PP 材質，微波/冷凍可用", 999, 45, randomTags(tags, "生活用品", "食品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/A9D6691C11-SP-12324249.jpg"));
        products.add(createProduct("雀巢 Nespresso Essenza Mini", "19bar 高壓萃取，輕巧機身", 3990, 22, randomTags(tags, "家電", "食品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/42725_P_1604620808986.webp"));
        products.add(createProduct("Starbucks 經典隨行杯", "不鏽鋼 473ml，雙層真空保溫", 990, 50, randomTags(tags, "生活用品", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/0hGaoN8S0YGE5SNw7M2qxnGWthFD9hUw1IPE9RKH81TiwqVENNb1EENHJgQS1jVF4eaU0CKSBiFH1_V1caaQQ.jpg"));
        products.add(createProduct("Brita 濾水壺 3.5L", "MAXTRA+ 濾芯，減少水垢", 1490, 30, randomTags(tags, "生活用品", "家電"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/c3dd266501ec74ff85866b2d80a44e52-850x850.jpg"));
        products.add(createProduct("Hill's 希爾思 成貓飼料 3kg", "雞肉配方，室內貓專用", 1290, 40, randomTags(tags, "寵物用品", "食品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/8bfc65ea-e8cc-4d7f-bf65-a648e96fd50a.jpeg.webp"));
        products.add(createProduct("CROCI 貓抓板", "波浪造型，天然劍麻，含貓草", 399, 70, randomTags(tags, "寵物用品", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/800x2.png"));
        products.add(createProduct("YETI Rambler 真空保溫杯", "不鏽鋼 887ml，MagSlider 防漏蓋", 1390, 35, randomTags(tags, "運動戶外", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/800x.jpg"));
        products.add(createProduct("Mont-Bell 羽絨睡袋", "800FP 羽絨，輕量保暖，壓縮收納", 8990, 10, randomTags(tags, "運動戶外"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/1121383BLRI.webp"));
        products.add(createProduct("迪卡儂 露營帳篷 4 人", "防潑水，快搭設計，UV50+", 3990, 15, randomTags(tags, "運動戶外", "限時優惠"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/mobile01-0ae1b5543b511c2606fb9c66451ef460.jpg"));
        products.add(createProduct("The North Face 登山背包 30L", "透氣背負系統，多夾層設計", 4990, 18, randomTags(tags, "運動戶外", "新品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/BAA0AEBAF7-SP-8872594.jpg"));
        products.add(createProduct("Colapz 折疊水壺 500ml", "食品級矽膠，可折疊收納，輕量", 490, 60, randomTags(tags, "運動戶外", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/SCOLWATERB-----500x500.jpg"));
        products.add(createProduct("SwitchBot 智慧開關機器人", "語音控制，APP 遙控，排程定時", 990, 40, randomTags(tags, "電子產品", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/200306_switch_0.jpg"));
        products.add(createProduct("小米智慧攝影機 C300", "2K 畫質，360° 旋轉，夜視功能", 1190, 30, randomTags(tags, "電子產品", "寵物用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/ef37cc60ea796cb472101e2d75b2fb0e.png"));
        products.add(createProduct("Kobo Clara 2E 電子書閱讀器", "6 吋 E Ink，16GB，防水，暖光", 4990, 15, randomTags(tags, "電子產品", "書籍"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/1200-02-694x442.jpg"));
        products.add(createProduct("原子習慣：細微改變帶來巨大成就", "James Clear 暢銷書，習慣養成指南", 330, 80, randomTags(tags, "書籍", "熱賣"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/getImage.webp"));
        products.add(createProduct("人類大歷史：從野獸到扮演上帝", "Yuval Noah Harari 經典之作", 450, 50, randomTags(tags, "書籍"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/book1.webp"));
        products.add(createProduct("超簡單 Python 入門", "程式設計初學者指南，附練習題", 520, 40, randomTags(tags, "書籍", "辦公用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/F3768.jpg"));
        products.add(createProduct("3M 浴室無痕收納架", "防水無痕膠條，免鑽孔", 499, 65, randomTags(tags, "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/4710367988182_main_64023324_20210502161047_01_1200.jpg"));
        products.add(createProduct("花王 蒸氣眼罩 12 枚", "舒緩疲勞，薰衣草/玫瑰/無香", 299, 90, randomTags(tags, "生活用品", "美妝保養"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/e34917_0a53f401965241e99730acc2e088913b~mv2.avif"));
        products.add(createProduct("GATSBY 超強力髮蠟", "長效定型，不黏膩，光澤感", 199, 100, randomTags(tags, "美妝保養"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/images%20%281%29.jpg"));
        products.add(createProduct("Shell 機油 5W-40 1L", "全合成機油，高效引擎保護", 599, 50, randomTags(tags, "汽車用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/Ultra.webp"));
        products.add(createProduct("Michelin 打氣機", "數位顯示，USB 充電，LED 照明", 990, 30, randomTags(tags, "汽車用品", "運動戶外"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/12267.jpg"));
        products.add(createProduct("盆栽專用營養土 10L", "有機質豐富，透氣保水", 199, 70, randomTags(tags, "園藝"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/QrFcmPJaBx20230710103821.png"));
        products.add(createProduct("不鏽鋼園藝工具 5 件組", "鏟/耙/剪/鋤/手套，收納袋", 799, 35, randomTags(tags, "園藝", "新品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/54a15ceb83c340a1a792d62f3f9b4c3a.jpg"));
        products.add(createProduct("小米空氣淨化器 4 Pro", "CADR 500m³/h，OLED 觸控螢幕", 6990, 12, randomTags(tags, "家電", "新品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/section14Img1.png"));
        products.add(createProduct("Google Nest Hub 2", "7 吋智慧螢幕，睡眠感應，Google 助理", 3490, 18, randomTags(tags, "電子產品", "生活用品"), "https://dfpz032vsuqyxbfx.public.blob.vercel-storage.com/1626343853128341251.jpg"));
        productRepository.saveAll(products);
        System.out.println("✅ " + products.size() + " 個商品已建立");

        // ==================== 優惠券 ====================
        couponRepository.saveAll(List.of(
                createCoupon("WELCOME50", DiscountType.FIXED_AMOUNT, 50, 500, null, 200),
                createCoupon("SUMMER10", DiscountType.PERCENTAGE, 10, 300, 200, 100),
                createCoupon("VIP100", DiscountType.FIXED_AMOUNT, 100, 1000, null, 50),
                createCoupon("NEWYEAR20", DiscountType.PERCENTAGE, 20, 800, 300, 80),
                createCoupon("FREESHIP", DiscountType.FIXED_AMOUNT, 60, 0, null, 500)
        ));
        System.out.println("✅ 5 個優惠券已建立");

        // ==================== 訂單 ====================
        OrderStatus[] statuses = {OrderStatus.PENDING, OrderStatus.PAID, OrderStatus.SHIPPED, OrderStatus.COMPLETED, OrderStatus.CANCELLED};
        String[] addresses = {"台北市大安區", "新北市板橋區", "台中市西屯區", "高雄市左營區", "台南市東區"};

        for (int i = 1; i <= 35; i++) {
            User buyer = users.get(random.nextInt(4) + 1); // buyer1-buyer3 或 visitor
            Order order = Order.builder()
                    .user(buyer)
                    .status(statuses[random.nextInt(statuses.length)])
                    .totalAmount(BigDecimal.ZERO)
                    .createdAt(LocalDateTime.now().minusDays(random.nextInt(60)).minusHours(random.nextInt(24)))
                    .build();

            int itemCount = random.nextInt(4) + 1;
            BigDecimal total = BigDecimal.ZERO;
            List<OrderItem> items = new ArrayList<>();

            for (int j = 0; j < itemCount; j++) {
                Product product = products.get(random.nextInt(products.size()));
                int qty = random.nextInt(3) + 1;
                BigDecimal unitPrice = product.getPrice();
                BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(qty));
                total = total.add(subtotal);

                OrderItem item = OrderItem.builder()
                        .order(order)
                        .product(product)
                        .quantity(qty)
                        .unitPrice(unitPrice)
                        .subtotal(subtotal)
                        .build();
                items.add(item);
            }

            order.setTotalAmount(total);
            order.setItems(items);
            orderRepository.save(order);
        }
        System.out.println("✅ 35 筆訂單已建立");
    }

    private User createUser(String username, String email, String password, UserRole role) {
        return User.builder()
                .username(username)
                .email(email)
                .password(passwordEncoder.encode(password))
                .role(role)
                .build();
    }

    private Tag createTag(String name, String color) {
        return Tag.builder().name(name).color(color).build();
    }

    private Set<Tag> randomTags(List<Tag> allTags, String... required) {
        Set<Tag> result = new HashSet<>();
        for (String name : required) {
            allTags.stream().filter(t -> t.getName().equals(name)).findFirst().ifPresent(result::add);
        }
        if (random.nextBoolean()) {
            allTags.stream().filter(t -> !result.contains(t)).findAny().ifPresent(result::add);
        }
        return result;
    }

    private Product createProduct(String name, String desc, int price, int stock, Set<Tag> tags, String ImgUrl) {
        return Product.builder()
                .name(name)
                .description(desc)
                .price(BigDecimal.valueOf(price))
                .stock(stock)
                .imageUrl(ImgUrl)
                .tags(tags)
                .build();
    }

    private Coupon createCoupon(String code, DiscountType type, int value, int minPurchase, Integer maxDiscount, int quantity) {
        return Coupon.builder()
                .code(code)
                .discountType(type)
                .discountValue(BigDecimal.valueOf(value))
                .minPurchase(BigDecimal.valueOf(minPurchase))
                .maxDiscount(maxDiscount != null ? BigDecimal.valueOf(maxDiscount) : null)
                .quantity(quantity)
                .expireAt(LocalDateTime.now().plusMonths(3))
                .build();
    }
}