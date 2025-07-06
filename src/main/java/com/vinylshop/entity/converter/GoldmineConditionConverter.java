package com.vinylshop.entity.converter;

import com.vinylshop.entity.GoldmineCondition;
import com.vinylshop.util.GoldmineUtil;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class GoldmineConditionConverter implements AttributeConverter<GoldmineCondition, String> {

    @Override
    public String convertToDatabaseColumn(GoldmineCondition condition) {
        return condition == null ? null : condition.code();
    }

    @Override
    public GoldmineCondition convertToEntityAttribute(String code) {
        return code == null ? null : GoldmineUtil.fromCode(code);
    }

}
