package org.example.fileIO;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.data.Company;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileIO {

    public static final  String NO_EMAILS = "companiesWithoutEmails.json";
    public static final  String FULL_LIST = "companiesFullList.json";
    public static final  String FULL_INFO_LIST = "companiesWithFullInfoList.json";


    public void createFile(String fileName){
        File companyFile = new File(fileName);
        try {
            if(!companyFile.exists()) {
                companyFile.createNewFile();
            }
        } catch (IOException e){
            System.err.println("Error occurred");
            e.printStackTrace();
        }
    }


    public List<Company> readInfoCompaniesWithNoEmails(){
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(NO_EMAILS);
        List<Company> companiesList = new ArrayList<>();
        try{
            companiesList = mapper.readValue(file, new TypeReference<List<Company>>() {
            });
        } catch (IOException e){
            e.printStackTrace();
        }
        return companiesList;
    }

    public void saveCompaniesWithNoEmails(List<Company> companiesList){
        createFile(NO_EMAILS);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(NO_EMAILS);
        try {
            mapper.writeValue(file, companiesList);
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public void saveCompaniesFullList(List<Company> companiesWithFullInfo) {
        createFile(FULL_LIST);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(FULL_LIST);
        try {
            mapper.writeValue(file, companiesWithFullInfo);
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<Company> readFileFullList() {
        ObjectMapper mapper = new ObjectMapper();
        List<Company> companiesList = new ArrayList<>();
        File file = new File(FULL_LIST);
        if(!file.exists()){
            createFile(FULL_INFO_LIST);
            return companiesList;
        }
        try{
            companiesList = mapper.readValue(file, new TypeReference<List<Company>>() {
            });
        } catch (IOException e){
            e.printStackTrace();
        }
        return companiesList;
    }

    public void saveCompaniesFullInfoList(List<Company> companiesWithFullInfoList) {
        createFile(FULL_LIST);
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        File file = new File(FULL_INFO_LIST);
        try {
            mapper.writeValue(file, companiesWithFullInfoList);
        } catch (IOException e){
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public List<Company> readFileFullInfoList() {
        ObjectMapper mapper = new ObjectMapper();
        List<Company> companiesList = new ArrayList<>();
        File file = new File(FULL_INFO_LIST);
        if(!file.exists()){

            createFile(FULL_INFO_LIST);
            return companiesList;
        }
        try{
            companiesList = mapper.readValue(file, new TypeReference<List<Company>>() {
            });
        } catch (IOException e){
            e.printStackTrace();
        }
        return companiesList;
    }
}
