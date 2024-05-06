from flask import Flask, request, jsonify
import joblib

# Load the trained model
model = joblib.load(r'C:\Users\sofie\Desktop\backKhademni-main\backKhademni-main\venv\models\chatbot_model.pkl')

app = Flask(__name__)

@app.route('/predict', methods=['POST'])
def predict():
    # Get the question from the incoming request
    data = request.json
    question = data.get('question', '')

    # Use the trained model to predict the response
    response = model.predict([question])[0]

    return jsonify({'response': response})  # Return the response in JSON format

if __name__ == '__main__':
    # Run the Flask service on port 5000
    app.run(port=5000)
