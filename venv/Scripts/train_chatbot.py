 
import re
import pandas as pd
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from sklearn.pipeline import Pipeline
import joblib  # For saving/loading models

# Download necessary NLTK data
nltk.download('stopwords')
nltk.download('punkt')

data = {
    'question': [
	"I bought a service on a freelance marketplace, but the freelancer missed the deadline and the quality was poor. Attempts to get a refund or support have been fruitless, adding to my dissatisfaction.",  
        "I recently purchased a service on a freelance platform, but the deliverable wasn't as promised. Customer support has been slow to respond, leaving me frustrated and seeking a resolution.",          	"What are the terms for filing a reclamation?",  # Reclamation policy
        "How do I resolve a freelance dispute?",  # Dispute resolution
        "Can I get a refund for a freelance service?",  # Refund policies
        "How can I contact customer support?",  # Contacting support
        "What should I do if a freelancer doesn't deliver?",  # Freelancer not delivering
        "How do I escalate a freelance issue?",  # Escalating issues
        "What's the process for getting a refund?",  # Refund process
        "Is there a timeline for resolving freelance disputes?",  # Dispute resolution timeline
        "How do I leave feedback for a freelancer?",  # Providing feedback
        "What happens if a freelancer doesn't respond?",  # No response from freelancer
        "What if the freelancer is unprofessional?",  # Unprofessional freelancer,
	"Hello!",
    "How are you?",
    "What's your name?",
    "What's the weather like?",
    "Can you tell me a joke?",
    "What's your favorite color?",
    "What's your favorite sport?",
    "Do you like music?",
    "Can you play games?",
    "What's your favorite food?",
    "Can you help me with my project?",
    "What's the meaning of life?",
    "Why is the sky blue?",
    "What's your favorite movie?",
    "Can you tell me something interesting?",
    "What's your favorite book?",
    "What's the time?",
    "How do I reset my password?",
    "How do I update my profile?",
    "How do I contact customer support?",
    "What's the capital of France?",
    "What's the capital of Germany?",
    "What's your purpose?",
    "How do I get help?",
    "Can you tell me a story?",
    "How do I become a freelancer?",
    "How do I post a job?",
    "How do I set freelance rates?",
    "What documents are needed for reclamation?",
    "What is a reclamation ID?",
    "How do I dispute a reclamation decision?",
    "What is the process for reclamation?",
    "How do I get reviews and ratings?",
    "How do I withdraw earnings?",
    "Can I work as a freelancer while employed?",
    "How do I manage freelance projects?",
    "What skills are in demand?",
    "How do I contact clients?",
    "How do I ensure secure payments?",
    "What's your favorite animal?",
    "Do you have any hobbies?",
    "Can you recommend a good book?",
    "How do I learn programming?",
    "What's the best platform for freelancers?",
    "What's your opinion on AI?",
    "What's your favorite programming language?",
    "What are your business hours?",
    "Can I file multiple reclamations?",
    "What's your advice for freelancers?",
    "What is the best way to find clients?"
        "How can I file a claim?",
        "What is the status of my claim?",
        "Tell me about freelance opportunities",
        "How do I become a freelancer?",
        "How do I post a job?",
        "What skills are in demand?",
        "How do I hire a freelancer?",
        "How do I set freelance rates?",
        "How do I create a freelancer profile?",
        "What are the fees for freelancers?",
        "How do I receive payments as a freelancer?",
        "What is the best way to find clients?",
        "Can I work as a freelancer while employed?",
        "How do I ensure secure payments?",
        "What are the rules for freelancers?",
        "How do I contact clients?",
        "What is a freelance contract?",
        "How do I dispute a freelance payment?",
        "How do I get reviews and ratings?",
        "What is the best platform for freelancers?",
        "How do I withdraw earnings?",
        "Can I work internationally as a freelancer?",
        "How do I manage freelance projects?",
        "How do I file a reclamation?",
        "What is the process for reclamation?",
        "How long does it take to resolve a reclamation?",
        "What documents are needed for reclamation?",
        "How do I track my reclamation?",
        "What is the status of my reclamation?",
        "What if my reclamation is denied?",
        "How do I appeal a reclamation decision?",
        "What types of reclamations are accepted?",
        "How do I withdraw a reclamation?",
        "Can I file multiple reclamations?",
        "What is a reclamation ID?"
    ],
    'response': [
	"We're sorry to hear about your experience. Please provide more details to help us resolve the issue. You can contact our support team for further assistance.",  # Response for poor freelance quality
        "We understand your frustration. You can file a reclamation or initiate a dispute. Please contact our support team for guidance.",  # Response for incomplete freelance work
        "We apologize for the inconvenience. You can initiate a complaint process through our support team. Please provide the project details to expedite the resolution.",  # Response for delayed deliverables
        "To file a reclamation, you need to provide proof of the issue and any related communication. Contact our support team for more information.",  # Reclamation process
        "To resolve a freelance dispute, you can initiate a mediation process through our platform. Contact support for more details.",  # Dispute resolution process
        "Refunds depend on the contract terms and the nature of the issue. Please contact our support team to check your eligibility for a refund.",  # Refund eligibility
        "You can contact our customer support through email or live chat on our website. We're here to help.",  # Contacting support
        "If a freelancer doesn't deliver, you can escalate the issue through our platform's dispute resolution process.",  # Escalating non-delivery
        "To escalate an issue, contact our support team and explain your situation. We will guide you through the next steps.",  # Escalation process
        "To get a refund, provide proof of the issue and follow the refund process outlined on our platform.",  # Refund process
        "The timeline for resolving freelance disputes depends on the complexity of the case. Generally, it takes a few days to a couple of weeks.",  # Dispute resolution timeline
        "To leave feedback, visit the freelancer's profile and use the feedback section. Be honest and constructive.",  # Leaving feedback
        "If a freelancer doesn't respond, contact support. We will reach out to the freelancer on your behalf.", 
	"Hello! How can I assist you today?",
    "I'm just a chatbot, but I'm here to help you!",
    "I'm a virtual assistant.",
    "I don't know about the weather, but I hope it's nice!",
    "Why did the computer break up with the calculator? It found it too calculating!",
    "I like all colors equally.",
    "I'm not really into sports, but I support all teams!",
    "I love music, but I don't have a playlist.",
    "I don't play games, but I can help you with other things.",
    "I don't eat, but I hear pizza is great!",
    "I can guide you with some tips for your project.",
    "The meaning of life is to ask questions!",
    "It's because of Rayleigh scattering.",
    "I don't watch movies, but I hear 'Inception' is good!",
    "Did you know that honey never spoils?",
    "I don't read books, but I know a lot about technology!",
    "You can check the time on your device.",
    "To reset your password, click 'Forgot Password' on the login page.",
    "To update your profile, go to 'Account Settings'.",
    "Contact customer support through our website or phone number.",
    "The capital of France is Paris.",
    "The capital of Germany is Berlin.",
    "I'm here to make your life easier.",
    "Just ask me, and I'll try to help.",
    "I don't know any stories, but I can give you some tips.",
    "To become a freelancer, create an account and complete your profile.",
    "To post a job, go to the 'Jobs' section and click 'Post a Job'.",
    "Freelance rates vary by skill, experience, and project complexity.",
    "Required documents for reclamation include proof of purchase, invoices, and correspondence.",
    "A reclamation ID is a unique identifier for your claim.",
    "Dispute a reclamation by contacting support and providing evidence.",
    "The reclamation process includes filing, documentation, and resolution.",
    "Get reviews and ratings by completing projects and requesting feedback.",
    "Withdraw earnings by linking your bank account or PayPal.",
    "Yes, you can work as a freelancer while employed, but check your employment contract.",
    "Use project management tools to manage freelance projects.",
    "In-demand skills include web development, graphic design, writing, and data analysis.",
    "Contact clients through the platform's messaging system.",
    "Use escrow services for secure payments.",
    "I like cats and dogs equally.",
    "I enjoy learning new things.",
    "I can suggest programming books.",
    "You can learn programming through online courses and tutorials.",
    "Popular platforms for freelancers include Upwork, Freelancer, and Fiverr.",
    "AI is a fascinating field with a lot of potential!",
    "Python is a popular programming language.",
    "We're available 24/7.",
    "Yes, you can file multiple reclamations for different issues.",
    "My advice for freelancers: be professional and communicate clearly.",
    "You can find clients by creating a strong profile and networking."
        "To file a claim, visit our claims page or contact support.",
        "To check your claim status, please provide your claim ID.",
        "Our freelance marketplace connects freelancers and clients for projects.",
        "To become a freelancer, create an account and complete your profile.",
        "To post a job, go to the 'Jobs' section and click 'Post a Job'.",
        "In-demand skills include web development, graphic design, writing, and data analysis.",
        "To hire a freelancer, create a project and post a job description.",
        "Freelance rates vary by skill, experience, and project complexity.",
        "Create a freelancer profile by providing your skills, experience, and portfolio.",
        "Fees for freelancers are typically a percentage of your earnings.",
        "Freelancers can receive payments via PayPal, bank transfer, or other methods.",
        "Find clients by creating a strong profile and applying to jobs.",
        "Yes, you can work as a freelancer while employed, but check your employment contract.",
        "Use escrow services and reliable payment platforms for secure payments.",
        "Freelancers must follow platform rules and maintain professionalism.",
        "Contact clients through the platform's messaging system.",
        "A freelance contract outlines the terms of the project and payment.",
        "Dispute a payment by contacting platform support and providing evidence.",
        "Get reviews and ratings by completing projects and requesting feedback.",
        "Popular platforms include Upwork, Freelancer, and Fiverr.",
        "Withdraw earnings by linking your bank account or PayPal.",
        "Yes, freelancers can work internationally, but check tax and legal requirements.",
        "Manage freelance projects by using project management tools and clear communication.",
        "To file a reclamation, submit your claim on our website or contact support.",
        "The reclamation process includes filing, documentation, and resolution.",
        "Reclamations typically take 1-2 weeks to resolve, depending on complexity.",
        "Required documents for reclamation include proof of purchase, invoices, and correspondence.",
        "Track your reclamation by logging into your account and viewing the status.",
        "To check the status of your reclamation, provide your reclamation ID.",
        "If your reclamation is denied, you can appeal by providing additional evidence.",
        "Appeal a reclamation decision by submitting an appeal form with supporting documents.",
        "Accepted reclamations include product defects, service issues, and billing errors.",
        "To withdraw a reclamation, contact support and explain your reason.",
        "Yes, you can file multiple reclamations for different issues.",
        "A reclamation ID is a unique identifier for your claim."
    ]
}
# Create DataFrame from the training data
df = pd.DataFrame(data)

# Preprocess and tokenize text
df['question'] = df['question'].str.lower()  # Convert to lowercase
df['question'] = df['question'].apply(lambda x: re.sub(r'[^\w\s]', '', x))  # Remove punctuation
df['tokens'] = df['question'].apply(word_tokenize)  # Tokenize

# Set up TF-IDF and MultinomialNB in a pipeline
pipeline = Pipeline([
    ('tfidf', TfidfVectorizer()),
    ('classifier', MultinomialNB())
])

# Train the pipeline
pipeline.fit(df['question'], df['response'])

# Save the model to disk
joblib.dump(pipeline, '../models/chatbot_model.pkl')  # Save in 'models/' directory