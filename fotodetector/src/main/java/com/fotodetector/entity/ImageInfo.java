package com.fotodetector.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;



@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "images")
public class ImageInfo  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;
    private String fileName;
    private String fileType;

    @Lob
    private byte[] data;


}
