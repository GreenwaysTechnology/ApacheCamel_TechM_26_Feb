package com.example;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "camel_file_log")
public class CamelFileLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name")
    private String fileName;

    private int records;

    private String status;

    @Column(name = "processed")
    private LocalDateTime processed = LocalDateTime.now();

    // ── Constructors ──────────────────────────────────────────────────────────
    public CamelFileLog() {}

    public CamelFileLog(String fileName, int records, String status) {
        this.fileName  = fileName;
        this.records   = records;
        this.status    = status;
        this.processed = LocalDateTime.now();
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────
    public Long          getId()                   { return id; }
    public String        getFileName()             { return fileName; }
    public void          setFileName(String f)     { this.fileName = f; }
    public int           getRecords()              { return records; }
    public void          setRecords(int r)         { this.records = r; }
    public String        getStatus()               { return status; }
    public void          setStatus(String s)       { this.status = s; }
    public LocalDateTime getProcessed()            { return processed; }
    public void          setProcessed(LocalDateTime p) { this.processed = p; }
}