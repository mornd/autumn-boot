package com.mornd.system.utils;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.ReadConverterContext;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.mornd.system.constant.enums.EnumGenderType;
import com.mornd.system.entity.po.base.BaseEntity;


/**
 * @author: mornd
 * @dateTime: 2023/1/31 - 18:41
 * 自定义 easyExcel 类型转换器
 */
public class EasyExcelConverter {
    /**
     * 性别转换
     */
    public static class GenderConverter implements Converter<Integer> {
        /**
         * java 类型
         * @return
         */
        @Override
        public Class<?> supportJavaTypeKey() {
            return Integer.class;
        }

        /**
         * excel 写入类型
         * @return
         */
        @Override
        public CellDataTypeEnum supportExcelTypeKey() {
            return CellDataTypeEnum.STRING;
        }

        @Override
        public Integer convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            return Converter.super.convertToJavaData(cellData, contentProperty, globalConfiguration);
        }

        @Override
        public Integer convertToJavaData(ReadConverterContext<?> context) throws Exception {
            return Converter.super.convertToJavaData(context);
        }

        @Override
        public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            WriteCellData<String> cellData = new WriteCellData<>();
            cellData.setType(CellDataTypeEnum.STRING);
            if(EnumGenderType.FEMALE.getCode().equals(value)) {
                cellData.setStringValue(EnumGenderType.FEMALE.getName());
            } else if (EnumGenderType.MALE.getCode().equals(value)) {
                cellData.setStringValue(EnumGenderType.MALE.getName());
            } else {
                cellData.setStringValue("");
            }
            return cellData;
        }

        @Override
        public WriteCellData<?> convertToExcelData(WriteConverterContext<Integer> context) throws Exception {
            return Converter.super.convertToExcelData(context);
        }
    }

    /**
     * 状态转换
     */
    public static class StatusConverter implements Converter<Integer> {
        @Override
        public WriteCellData<?> convertToExcelData(Integer value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
            WriteCellData<String> cellData = new WriteCellData<>();
            cellData.setType(CellDataTypeEnum.STRING);
            if(BaseEntity.EnableState.ENABLE.getCode().equals(value)) {
                cellData.setStringValue(BaseEntity.EnableState.ENABLE.getName());
            } else if (BaseEntity.EnableState.DISABLE.getCode().equals(value)) {
                cellData.setStringValue(BaseEntity.EnableState.DISABLE.getName());
            } else {
                cellData.setStringValue("");
            }
            return cellData;
        }
    }
}
