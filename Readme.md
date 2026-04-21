# AI-Powered Skin Cancer Detection System

An end-to-end deep learning system for **skin lesion classification**, combining AI, backend APIs, and full-stack deployment into a real-world clinical workflow.

---

## 🚀 Live Demo

* Web App: [Website Link](https://skincancerdetection-dn2amt9pjku6r7ge94q7xd.streamlit.app/)

* Android App: [Linkedin post showing the demo](https://www.linkedin.com/posts/ankit-yadav-67b124360_ai-machinelearning-deeplearning-ugcPost-7451949248981524480-Ke6N?utm_source=share&utm_medium=member_desktop&rcm=ACoAAFm4DL8Bo5rNM_-UZkVe061ljEJc5TqhFxY)

---

## 📌 Overview

This project classifies skin images into four categories:

* Benign Tumors
* Moles
* Normal Skin
* Skin Cancer

Designed as a **scalable, low-resource AI system** with web and mobile interfaces.

---

## 🔬 Model

* **Architecture**: CNN (TensorFlow / Keras)
* **Input Size**: 256 × 192 × 3 (RGB)
* **Preprocessing**:

  * Resizing
  * RGB conversion
  * Center cropping using ImageOps.fit
* **Test Accuracy**: 82.80%
* **Export Format**: `.keras`

---

## 🔌 Backend API

* **Framework**: FastAPI
* **Server**: Uvicorn
* **Deployment**: Render

### Endpoints

* `GET /ping` → Health check
* `POST /predict` → Image classification

### Response Format

```json
{
  "class": "SkinCancer",
  "confidence": 0.87
}
```

### Optimization

* Uses `tensorflow-cpu`
* Runs within ~512MB RAM
* Thread usage controlled via environment variables

---

## 🎨 Web Interface (Streamlit)

* Clean clinical UI
* Image upload + preview
* Real-time prediction
* Confidence visualization
* Custom CSS styling

---

## 📱 Mobile Application

* **Platform**: Android (Kotlin, Jetpack Compose)
* **Features**:

  * Camera integration using Android Intent
  * Retrofit for API calls
  * Coil for image loading and caching
  * Real-time prediction display

---

## ☁️ Deployment

| Layer   | Tech Stack              |
| ------- | ----------------------- |
| Model   | TensorFlow, Keras       |
| API     | FastAPI, Uvicorn        |
| Backend | Render                  |
| Web     | Streamlit Cloud         |
| Mobile  | Kotlin, Jetpack Compose |

* Fully decoupled frontend & backend
* Optimized for low-resource environments

---

## 📂 Project Structure

```
Skin_cancer_detection/
│
├── models/
├── api/
├── website/
├── app/
├── requirements.txt
└── README.md
```

---

## ⚙️ Setup Instructions

### 1. Clone Repo

```bash
git clone https://github.com/Ankit6321/Skin_cancer_detection.git
cd Skin_cancer_detection
```

### 2. Run Backend

```bash
cd api
pip install -r requirements.txt
uvicorn main:app --reload
```

### 3. Run Frontend

```bash
cd website
pip install -r requirements.txt
streamlit run app.py
```

### 🔑 Note (Important)

The frontend expects an API URL.

The app automatically handles both cases:

* Uses `st.secrets["API_URL"]` in deployment
* Falls back to `http://127.0.0.1:8000` locally

Make sure backend is running before starting frontend.

---

## 🧪 Usage

1. Open the web app
2. Upload or capture an image
3. Click **Predict**
4. View classification result with confidence

---

## ⚠️ Disclaimer

This project is for **educational purposes only**.
Not intended for medical diagnosis.

---

## 🤝 Contributions

Pull requests and suggestions are welcome.

---

## 📬 Contact

Feel free to reach out for collaboration or feedback.
