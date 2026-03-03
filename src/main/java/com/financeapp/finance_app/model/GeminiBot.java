package com.financeapp.finance_app.model;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

public class GeminiBot {
    public static void main(String[] args) throws Exception {
        // The client gets the API key from the environment variable `GEMINI_API_KEY`.
        Client client = new Client();
        
        //Sends a request to the Gemini API to generate content.
        GenerateContentResponse response =
        client.models.generateContent(
            "gemini-3-flash-preview",
            "Explain how AI works in a few words",
            null);

        System.out.println(response.text());
        


    }

    
}
