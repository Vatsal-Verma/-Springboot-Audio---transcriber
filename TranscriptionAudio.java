package com.audio.audio_transcribe;

import java.io.File;
import java.io.IOException;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/transcribe")
public class TranscriptionController {
    
    private final OpenAiAudioTranscriptionModel transcriptionModel;

    public TranscriptionController(@Value("${spring.ai.openai.api-key}") String apiKey) {
        
        // OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi(System.getenv("spring.ai.openai.api-key"));4
        @SuppressWarnings("removal")
        OpenAiAudioApi openAiAudioApi = new OpenAiAudioApi(apiKey);
        this.transcriptionModel  = new OpenAiAudioTranscriptionModel(openAiAudioApi);
    }

    //end point explaination at 10:55:00
    @PostMapping
    public ResponseEntity<String> transcribeAudio(@RequestParam("file")MultipartFile file) throws IOException {
        
        File tempFile = File.createTempFile("audio", ".wav");
        file.transferTo(tempFile); //tranfer the content of the file to a newly created file

        OpenAiAudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                    .language("en")
                    .prompt("Ask not this, but ask that")
                    .temperature(0f)
                    .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
                    .build();
                    
                    FileSystemResource audioFile = new FileSystemResource(tempFile);
                
                    AudioTranscriptionPrompt transcriptionRequest = new AudioTranscriptionPrompt(audioFile, transcriptionOptions);
                    AudioTranscriptionResponse response = transcriptionModel.call(transcriptionRequest);
                    
                    tempFile.delete();
                    return new ResponseEntity<>(response.getResult().getOutput(), HttpStatus.OK);
    }

    
}
