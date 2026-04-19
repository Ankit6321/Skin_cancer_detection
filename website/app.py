import streamlit as st
import requests
from PIL import Image
import io

# 1. Page Configuration
st.set_page_config(page_title="SkinAI Clinical", page_icon="🩺", layout="wide")

# 2. Force Light Mode and Clinical Styling
st.markdown("""
    <style>
    /* Force the background to white/light grey */
    .stApp {
        background-color: #FFFFFF;
    }
    
    /* Center the main title and text */
    .reportview-container .main .block-container {
        max-width: 1000px;
        padding-top: 2rem;
    }

    h1 {
        color: #0F172A !important;
        font-family: 'Inter', sans-serif;
        font-weight: 800;
        font-size: 3rem !important;
        margin-bottom: 0px !important;
    }

    .subtitle {
        color: #64748B;
        text-align: center;
        margin-bottom: 3rem;
        font-size: 1.1rem;
    }

    /* Analyze Button - Blue from your image */
    .stButton>button {
        width: 100%;
        background-color: #1D70CB !important;
        color: white !important;
        border-radius: 10px;
        height: 3.5em;
        font-weight: bold;
        border: none;
        transition: 0.3s;
    }
    
    .stButton>button:hover {
        background-color: #1557a3 !important;
        box-shadow: 0 4px 12px rgba(29, 112, 203, 0.2);
    }

    /* Result Box - Mint Green */
    .result-box {
        background-color: #E2F9E9;
        border-left: 5px solid #34A853;
        padding: 20px;
        border-radius: 10px;
        margin-top: 20px;
    }

    /* Warning Box - Soft Pink */
    .disclaimer-box {
        background-color: #FFF0F0;
        border: 1px solid #FED7D7;
        color: #9B2C2C;
        padding: 15px;
        border-radius: 10px;
        font-size: 0.9rem;
        margin-top: 15px;
    }

    /* Card styling for the upload/action area */
    .clinical-container {
        background-color: #F8FAFC;
        border: 1px solid #E2E8F0;
        border-radius: 24px;
        padding: 40px;
    }
    
    /* Hide Streamlit elements */
    #MainMenu {visibility: hidden;}
    footer {visibility: hidden;}
    header {visibility: hidden;}
    </style>
    """, unsafe_allow_html=True)

# 3. Header
st.markdown("<h1 style='text-align: center;'>Skin Analysis</h1>", unsafe_allow_html=True)
st.markdown("<p class='subtitle'>Harnessing advanced clinical AI for preliminary dermatological assessment and classification.</p>", unsafe_allow_html=True)

# 4. Main Centered Layout
_, col_main, _ = st.columns([1, 8, 1])

with col_main:
    # Outer Card Container
    st.markdown('<div class="clinical-container">', unsafe_allow_html=True)
    
    left_ui, right_ui = st.columns([1.2, 1], gap="large")
    
    with left_ui:
        uploaded_file = st.file_uploader("Upload Lesion Image", type=["jpg", "png", "jpeg"], label_visibility="collapsed")
        if uploaded_file:
            image = Image.open(uploaded_file)
            st.image(image, use_container_width=True)
            st.markdown("<p style='color: #94A3B8; font-size: 0.7rem; margin-top: 5px;'>SOURCE: CLINICAL UPLOAD &nbsp;&nbsp; ID: #DX-8821</p>", unsafe_allow_html=True)
        else:
            # Placeholder for alignment
            st.markdown("<div style='height: 300px; background-color: #EDF2F7; border-radius: 15px; display: flex; align-items: center; justify-content: center; color: #A0AEC0;'>Upload a clinical image to begin analysis</div>", unsafe_allow_html=True)

    with right_ui:
        st.markdown("<p style='font-size: 0.9rem; color: #475569; font-weight: 600; margin-bottom: 10px;'>Diagnostic Action</p>", unsafe_allow_html=True)
        
        # Action Button
        if st.button("🔬 Start Clinical Assessment"):
            if uploaded_file:
                with st.spinner("Analyzing spectral patterns..."):
                    try:
                        buf = io.BytesIO()
                        image.save(buf, format="JPEG")
                        response = requests.post("http://localhost:8000/predict", files={"file": buf.getvalue()})
                        
                        if response.status_code == 200:
                            res = response.json()
                            st.markdown("<div style='margin-top: 30px; border-top: 1px solid #E2E8F0; padding-top: 10px;'></div>", unsafe_allow_html=True)
                            st.markdown("<p style='font-size: 0.7rem; letter-spacing: 1px; color: #94A3B8; font-weight: bold;'>ANALYSIS RESULTS</p>", unsafe_allow_html=True)
                            st.markdown(f"""
                                <div class="result-box">
                                    <p style="margin:0; font-size: 0.8rem; color: #2F855A; font-weight: bold;">STATUS CLASSIFICATION</p>
                                    <h3 style="margin:0; color: #1A202C;">Prediction: {res['class']}</h3>
                                </div>
                            """, unsafe_allow_html=True)
                            
                            st.markdown(f"""
                                <div class="disclaimer-box">
                                    ⚠️ This tool is created only for educational purposes and the prediction may not be accurate.
                                </div>
                            """, unsafe_allow_html=True)
                    except Exception as e:
                        st.error("Connection to diagnostic server failed. Ensure FastAPI is running.")
            else:
                st.warning("Please upload an image first.")
    
    st.markdown('</div>', unsafe_allow_html=True)

# 5. Footer (Maintained your requested text)
st.markdown("<br><br><br><div style='border-top: 1px solid #E2E8F0; padding-top: 20px;'></div>", unsafe_allow_html=True)
st.markdown("<p style='font-size: 0.8rem; color: #64748B; text-align: left; margin-left: 10%;'>⚠️ Al prediction. Consult a professional dermatologist.</p>", unsafe_allow_html=True)