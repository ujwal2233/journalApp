package com.ujuj.journalApp.entity;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;

@Document(collection = "Journal_entries")
@Data
@NoArgsConstructor
public class JournalEntry {


    @Id
    private ObjectId id;
    @NonNull
    private  String title;
    private String content;
    private LocalDateTime date;




}
