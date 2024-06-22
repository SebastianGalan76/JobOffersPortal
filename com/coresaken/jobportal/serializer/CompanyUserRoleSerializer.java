package com.coresaken.jobportal.serializer;

import com.coresaken.jobportal.database.model.CompanyUserRole;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

public class CompanyUserRoleSerializer extends JsonSerializer<CompanyUserRole> {
    @Override
    public void serialize(CompanyUserRole companyUserRole, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeNumberField("company_id", companyUserRole.getCompany().getId());
        jsonGenerator.writeStringField("company_name", companyUserRole.getCompany().getName());
        jsonGenerator.writeStringField("company_logoUrl", companyUserRole.getCompany().getLogoUrl());
        jsonGenerator.writeStringField("company_backgroundUrl", companyUserRole.getCompany().getBackgroundUrl());
        jsonGenerator.writeNumberField("user_id", companyUserRole.getUser().getId());
        jsonGenerator.writeStringField("user_email", companyUserRole.getUser().getEmail());
        jsonGenerator.writeStringField("role", companyUserRole.getRole().name());
        jsonGenerator.writeEndObject();
    }
}