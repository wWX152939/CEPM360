package com.pm360.cepm360.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class FromFile {
    private byte[] data;
    private File file;
    
    private InputStream inputStream;
    
    private String fileName ;
    private String parameterName;
    private String contentType="application/octet-stream";
    
    public FromFile(String fileName, byte[] data, String parameterName, String contentType) {
        this.fileName = fileName;
        this.parameterName = parameterName;
        this.data = data;
        
        if (contentType != null){
            this.contentType = contentType;
        }
    }
    
    public FromFile(String fileName, File file, String parameterName, String contentType) {
        this.fileName = fileName;
        this.parameterName = parameterName;
        this.file = file;
        
        try {
            this.inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        
        if (contentType!=null) {
            this.contentType = contentType;
        }
    }
    
    public File getFile() {
        return file;
    }
    
    public InputStream getInputStream() {
        return inputStream;
    }
    
    public byte[] getData() {
        return data;
    }
    
    public String getFileName(){
        return fileName;
    }
    
    public void setFileName(String filename){
        this.fileName=filename;
    }
    
    public String getParameterName(){
        return parameterName;
    }
    
    public void setParameterName(String value){
        this.parameterName=value;
    }
    
    public String getContentType(){
        return contentType;
    }
    
    public void setContentType(String value){
        this.contentType=value;
    }
}
