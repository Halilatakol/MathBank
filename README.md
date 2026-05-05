# 📚 MathBank - Matematik Soru Bankası Uygulaması

PDF'deki matematik ve geometri sorularını Claude AI ile otomatik analiz ederek soru bankası oluşturan Android uygulaması.

---

## 🚀 Özellikler

- 📄 **PDF Analizi** — PDF'yi seçin, Claude AI her sayfayı görüntü olarak analiz eder
- 🔍 **Otomatik Soru Tespiti** — Numaralı, çizgili, boşluklu, karma düzen — hepsini destekler
- 🏷️ **Otomatik Sınıflandırma** — Konu, alt konu ve zorluk seviyesi AI tarafından belirlenir
- 🖼️ **Görsel Destek** — Geometrik şekil ve grafik içeren sorular görsel olarak kaydedilir
- 🗂️ **Akıllı Filtreleme** — Konuya, zorluk seviyesine, arama metnine göre filtrele
- ✏️ **Esnek Test Oluşturma** — Rastgele, konuya göre veya zorluk seviyesine göre test
- ⏱️ **Zamanlı Test** — İsteğe bağlı süre limiti ile test çöz
- 📊 **Konu Analizi** — Test sonrası hangi konuda ne kadar başarılısın?

---

## 🛠️ Kurulum

### 1. Projeyi Android Studio'da Açın
```
File → Open → MathQuestionBank klasörünü seçin
```

### 2. Claude API Anahtarı Alın
1. https://console.anthropic.com adresine gidin
2. Hesap oluşturun (ücretsiz $5 kredi)
3. API Keys → Create Key
4. Anahtarı kopyalayın (`sk-ant-api03-...`)

### 3. API Anahtarını Uygulamaya Girin
Uygulamayı açtıktan sonra:
- **Ana sayfa → ⚙️ Ayarlar → API Anahtarı** bölümüne yapıştırın

> ⚠️ API anahtarını kaynak koduna yazmayın! Uygulama içi ayarlardan girin.

### 4. Derleyin ve Çalıştırın
- Android 8.0 (API 26) ve üzeri gerekli
- Minimum 2GB RAM önerilir (PDF işleme için)

---

## 📁 Proje Yapısı

```
app/src/main/java/com/mathbank/
├── MathBankApp.kt                     ← Application sınıfı
├── ai/
│   └── ClaudeService.kt              ← Claude Vision API entegrasyonu
├── data/
│   ├── db/AppDatabase.kt             ← Room database, DAO'lar, converter'lar
│   ├── model/
│   │   ├── Question.kt               ← Soru entity modeli
│   │   └── Models.kt                 ← Test, filtre, progress modelleri
│   └── repository/
│       ├── QuestionRepository.kt     ← Veri katmanı (DB işlemleri)
│       └── SettingsManager.kt        ← DataStore ile ayar yönetimi
├── pdf/
│   └── PdfProcessor.kt               ← PDF → Bitmap dönüştürücü
└── ui/
    ├── activities/
    │   ├── MainActivity.kt           ← Ana ekran + istatistikler
    │   ├── ProcessingActivity.kt     ← PDF işleme + ilerleme ekranı
    │   ├── QuestionBankActivity.kt   ← Soru listesi + filtreler
    │   ├── CreateTestActivity.kt     ← Test oluşturma ekranı
    │   ├── SolveTestActivity.kt      ← Test çözme ekranı
    │   ├── TestResultActivity.kt     ← Sonuç ve analiz ekranı
    │   ├── QuestionDetailActivity.kt ← Soru detay ekranı
    │   └── SettingsActivity.kt       ← API key + tercihler
    ├── adapters/
    │   └── Adapters.kt               ← QuestionAdapter, TestAdapter
    └── viewmodels/
        ├── ProcessingViewModel.kt    ← PDF işleme iş mantığı
        └── ViewModels.kt             ← Diğer tüm ViewModel'lar
```

---

## 🔄 Çalışma Akışı

```
1. PDF SEÇ
   ↓ Android PDF Renderer ile her sayfa Bitmap'e çevrilir
   
2. CLAUDE AI ANALİZİ (sayfa başına)
   ↓ Bitmap → Base64 JPEG → Claude Vision API
   ↓ Prompt: "Bu sayfadaki tüm soruları JSON olarak döndür"
   ↓ Yanıt: [{text, topic, subtopic, difficulty, options, boundingBox...}]
   
3. GÖRSEL KIRPMA
   ↓ boundingBox koordinatları ile her sorunun görseli kırpılır
   
4. KAYDETME
   ↓ Soru + görsel → SQLite (Room DB)
   
5. FİLTRELEME & TEST
   ↓ Konu + Zorluk seç → Rastgele N soru → Test başlat
```

---

## 💰 API Maliyet Tahmini

| İşlem | Tahmini Maliyet |
|-------|----------------|
| 100 sayfalık PDF | ~$0.40-0.80 |
| 500 sayfalık PDF | ~$2-4 |
| 1000 soru çıkarma | ~$1-3 |

> Claude claude-opus-4-5 modeli kullanılmaktadır. Fiyatlar değişebilir.

---

## ⚙️ Konfigürasyon

### Desteklenen Konular (AI Otomatik Belirler)
- Cebir
- Geometri
- Analitik Geometri
- Trigonometri
- Analiz / Türev / İntegral
- Sayılar Teorisi
- Olasılık / İstatistik
- Kombinatorik
- Dizi / Seri
- Logaritma / Üstel Fonksiyonlar
- Fonksiyonlar

### Zorluk Seviyeleri
| Seviye | Açıklama |
|--------|----------|
| 🟢 Kolay | Tek adım, temel formül uygulama |
| 🟡 Orta | 2-3 adım, kavram anlama |
| 🔴 Zor | 4+ adım, çoklu konu, ispat |

---

## 🔧 Geliştirme Notları

### Rate Limiting
Sayfa başına 1.5 saniye bekleme vardır. Büyük PDF'lerde işlem şu şekilde sürer:
- 50 sayfa → ~2 dakika
- 200 sayfa → ~8 dakika
- 500 sayfa → ~20 dakika

### Bellek Yönetimi
Her sayfa işlendikten sonra Bitmap geri dönüştürülür (`bitmap.recycle()`).
`android:largeHeap="true"` manifest'te etkin.

### Görsel Kalitesi
- Sayfa render: 2x scale (150 DPI)
- Claude'a gönderilen görsel: max 1568px, JPEG %85
- Kaydedilen soru görseli: JPEG %80
- Kaydedilen tam sayfa: JPEG %60

---

## 📞 Destek

Herhangi bir hata veya öneri için lütfen bir issue açın.
