import streamlit as st
import requests
from PIL import Image
import io

try:
    requests.get("https://skin-disease-api-onsr.onrender.com/ping", timeout=1)
except:
    pass

url = st.secrets["API_URL"]

st.set_page_config(page_title="SkinAI Clinical", page_icon="🩺", layout="wide")

st.markdown("""
    <style>
    .stApp { background-color: #FFFFFF; font-family: 'Inter', sans-serif; }
    
    .block-container { 
        max-width: 1200px; 
        padding-top: 0.5rem !important;
    }

    h1 {
        color: #0F172A !important;
        font-weight: 800;
        font-size: 3rem !important;
        text-align: center;
        margin-top: 0px !important;
        margin-bottom: 5px !important;
    }
    .subtitle {
        color: #64748B;
        text-align: center;
        margin-bottom: 1.5rem;
        font-size: 1rem;
    }

    .clinical-container {
        background-color: #FFFFFF;
        border: 1px solid #E2E8F0;
        border-radius: 20px;
        padding: 30px;
        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
    }

    [data-testid="stFileUploader"] {
        background-color: #F8FAFC;
        border: 2px dashed #CBD5E1;
        border-radius: 12px;
        padding: 10px;
    }
    
    .stButton>button {
        width: 100%;
        background-color: #2563EB !important;
        color: white !important;
        border-radius: 8px;
        height: 3.2em;
        font-weight: 600;
        border: none;
    }

    .result-card {
        background-color: #F0FDF4;
        border: 1px solid #BBF7D0;
        padding: 20px;
        border-radius: 12px;
    }
    .result-value { color: #14532D; font-size: 1.6rem; font-weight: 800; margin: 0; }

    #MainMenu, footer, header { visibility: hidden; }
    </style>
    """, unsafe_allow_html=True)

st.markdown("<h1>Skin Analysis</h1>", unsafe_allow_html=True)
st.markdown("<p class='subtitle'>Advanced clinical AI for preliminary dermatological classification.</p>", unsafe_allow_html=True)

_, col_main, _ = st.columns([1, 10, 1])

with col_main:
    st.markdown('<div class="clinical-container">', unsafe_allow_html=True)
    left_ui, right_ui = st.columns([1, 1], gap="large")
    
    with left_ui:
        uploaded_file = st.file_uploader("Upload Lesion Image", type=["jpg", "png", "jpeg"], label_visibility="collapsed")
        if uploaded_file:
            image = Image.open(uploaded_file)
            st.image(image, use_container_width=True)
        else:
            st.markdown("<div style='height: 350px; background-color: #F1F5F9; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #94A3B8; border: 2px dashed #E2E8F0;'>Clinical Preview Area</div>", unsafe_allow_html=True)

    with right_ui:
        st.markdown("<p style='font-size: 0.85rem; color: #64748B; font-weight: 600;'>CONTROL PANEL</p>", unsafe_allow_html=True)
        
        if st.button("🔬 RUN SYSTEM ANALYSIS"):
            if uploaded_file:
                with st.spinner("Processing neural layers..."):
                    try:
                        buf = io.BytesIO()
                        image.save(buf, format="JPEG")
                        response = requests.post(url, files={"file": buf.getvalue()})
                        
                        if response.status_code == 200:
                            res = response.json()
                            pred_class = res['class']
                            conf = res['confidence'] * 100
                            
                            st.markdown(f"""
                                <div class="result-card">
                                    <p class="result-label">DIAGNOSTIC PREDICTION</p>
                                    <p class="result-value">{pred_class}</p>
                                    <div style="display: flex; justify-content: space-between; margin-top: 20px;">
                                        <span style="font-size: 0.8rem; color: #166534; font-weight: 600;">Confidence Level</span>
                                        <span style="font-size: 0.8rem; color: #166534; font-weight: 600;">{conf:.1f}%</span>
                                    </div>
                                    <div class="confidence-bar-bg">
                                        <div class="confidence-bar-fill" style="width: {conf}%;"></div>
                                    </div>
                                </div>
                            """, unsafe_allow_html=True)
                            
                            st.markdown("<p style='color: #94A3B8; font-size: 0.75rem; margin-top: 20px;'>⚠️ Analysis intended for educational evaluation. Consult a MD for diagnosis.</p>", unsafe_allow_html=True)
                    except Exception as e:
                        st.error("Connection failed. Check API deployment status.")
            else:
                st.info("Please upload a lesion image to initiate analysis.")
    st.markdown('</div>', unsafe_allow_html=True)

st.markdown("<p style='font-size: 0.8rem; color: #94A3B8; text-align: center; margin-top: 50px;'>Clinical Portal v1.0 • Protected by AES-256</p>", unsafe_allow_html=True)