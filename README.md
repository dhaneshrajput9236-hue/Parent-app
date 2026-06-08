# 🛡️ Offline Parent Control & Screen Time Manager 🛡️

**यह एक पूरी तरह से सुरक्षित, स्थानीय (Offline First) और बहु-आयामी पैरेंटल कंट्रोल और स्क्रीन समय लिमिटर एप्लीकेशन है।** 
*This is a fully secure, offline-first, and highly customizable Parent Control & Screen Time Limiter Application designed with Material 3 design principles.*

---

## 🚀 Key Highlights & Added Features (मुख्य विशेषताएं)

1. **Clean Initial Startup (बिल्कुल साफ शुरुआत) ✨**:
   - ऐप फ्रेश इंस्टॉल होने के बाद एकदम क्लीन खुलता है। कोई अनचाहा या नकली (Mock) डेटा पहले से उपस्थित नहीं होता है। आप अपनी सुविधानुसार बच्चों के नाम, अवतार और दैनिक समय-सीमा सेट कर सकते हैं।
   - *The application opens completely clean with zero pre-seeded default profiles or statistics, respecting user privacy and providing a fresh starts. User configures everything according to their custom requirements.*

2. **Anywhere Custom Time Set (समय कस्टमाइज़ करने की पूरी आज़ादी) ⏱️**:
   - **बच्चे की स्क्रीन लिमिट**: अब आप सेटिंग्स में जाकर किसी भी समय बच्चे की प्रोफ़ाइल का नाम और दैनिक सीमा बदल सकते हैं!
   - **ऐप-वार सीमा (App Limits Slider & Presets)**: `Rules` टैब में, हर एक ऐप के लिए आप अपनी मनपसंद समय सीमा स्लाइडर से 5 से 180 मिनट तक सेट कर सकते हैं, या क्विक प्रीसेट चिप्स (15m, 30m, 60m, 120m) चुन सकते हैं!
   - **कस्टम फोकस सत्र (Custom Focus Session Slider)**: फोकस टाइमर में आप 5 से 120 मिनट तक का कस्टम ध्यान सत्र शुरू कर सकते हैं।
   - *Users can set personalized timers and screen limits dynamically using smooth slider adjustments and quick preset chips for individual apps, child profiles, and focus timers.*

3. **Multi-Channel On-Device App Scanner (वास्तविक ऐप सिंक इंजन) 📲**:
   - डिवाइस में इंस्टॉल किए गए अन्य सभी वास्तविक और सिस्टम ऐप्स (Chrome, YouTube, WhatsApp आदि) को आसानी से `Scan Apps` दबाकर सिंक कर सकते हैं ताकि नियम बनाए जा सकें।
   - *Dynamically queries launcher intents and native packages of the client device to automatically synchronize configuration lists.*

4. **Demo Simulator (सैंडबॉक्स सिमुलेटर) 🧪**:
   - यदि आप एक खाली या एमुलेटर डिवाइस पर ऐप की रिपोर्ट स्क्रीन, 30 दिनों का सुंदर इंटरैक्टिव ग्राफ और ट्रांजैक्शन लॉग देखना चाहते हैं, तो सेटिंग्स में नीचे **Demo Mode (Load Demo Statistics)** चुनकर नकली डेटा लोड भी कर सकते हैं!
   - *An integrated sandbox system allowing developer/tester review of detailed charts without manual profile inputs.*

---

## 🛠️ GitHub-Friendly Setup & Automatic APK Building 📦
हमने इस प्रोजेक्ट को पूरी तरह से **GitHub Friendly** बना दिया है ताकि आप बिना किसी अतिरिक्त विन्यास के सीधे कोड अपलोड करके APK डाउनलोड कर सकें!

### 📥 1. GitHub Actions (Automatic APK Compile)
हमने **`.github/workflows/android.yml`** फ़ाइल जोड़ दी है। जब आप इस कोड को अपने GitHub पर पुश करेंगे:
1. GitHub अपने आप बैकग्राउंड में आपकी एप्लीकेशन को **तैयार (Compile)** करेगा।
2. **Actions** टैब में जाकर आप बिल्ड होने के बाद सीधे `parent-control-app-apk` ज़िप फ़ाइल डाउनलोड कर सकते हैं, जिसमें आपकी **APK** होगी।

### 🖥️ 2. Build via Local Terminal (घर बैठे बिल्ड करें)
यदि आप इसे अपने कंप्यूटर पर मैन्युअल रूप से बिल्ड करना चाहते हैं, तो इन आसान कमांड्स को रन करें:

```bash
# 1. रिपॉजिटरी को क्लोन करें (Clone Repository)
git clone <your-repository-url>
cd parent-control-app

# 2. कोड कंपाइल करें और डीबग APK तैयार करें (Build Debug APK)
gradle assembleDebug
```
तैयार एपीके फ़ाइल यहाँ प्राप्त होगी:  
📂 `app/build/outputs/apk/debug/app-debug.apk`

---

## 🎨 Visual Preview & Design Philosophy
यह ऐप **Material Design 3 Design System** और modern Jetpack Compose तकनीक पर आधारित है।
- **Cosmic Indigo Blue Accent Color**: गहरे नीले और जीवंत पेस्टल थीम के साथ आँखों की सुरक्षा।
- **Spacious Layout & Negative Space**: पढ़ने में बेहद आसान और साफ-सुथरा डिजाइन।
- **Touch Accessibility**: बटन और स्लाइडर का साइज न्यूनतम 48dp रखा गया है।
