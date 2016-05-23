package com.android.oneday.util;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * 一个通过解析XMl来得到解细的地址信息
 */
public class LocationXMLParser extends DefaultHandler {

    //定义为解析到了CountryName元素处,国家
    public static final int COUNTRYNAME = 1;
    //定义解析到了AdministrativeAreaName处，省份
    public static final int ADMINISTRATIVEAREANAME = 2;
    //定义解析到了LocalityName处， 城市
    public static final int LOCALITYNAME = 3;
    //定义解析到了DependentLocalityName处， 县区
    public static final int DEPENDENTLOCALITYNAME = 4;
    //判断是否存在地址
    private boolean hasAddress = false;

    //设置当前解析到了那一个元素
    private int element = 0;
    //保存详细的地理信息的Map
    private Map<Integer, String> locationInfo = new HashMap<Integer, String>();

    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        if (hasAddress) {
            //存在address时，检查它其中是否包含有值，如果没有则说明此地址不明
            if (ch == null || ch.length == 0) {
                hasAddress = false;
            }
        }

        //得到对应的内容
        String text = new String(ch);

        //有地址的情况下
        if (hasAddress) {
            switch (element) {
                case COUNTRYNAME:
                    text = text.substring(start, start + length);
                    locationInfo.put(COUNTRYNAME, text);
                    break;
                case ADMINISTRATIVEAREANAME:
                    //这里的减1是为了去掉省，市，区的后缀
                    text = text.substring(start, start + length - 1);
                    locationInfo.put(ADMINISTRATIVEAREANAME, text);
                    break;
                case LOCALITYNAME:
                    //这里的减1是为了去掉省，市，区的后缀
                    text = text.substring(start, start + length - 1);
                    locationInfo.put(LOCALITYNAME, text);
                    break;
                case DEPENDENTLOCALITYNAME:
                    //这里的减1是为了去掉省，市，区的后缀
                    text = text.substring(start, start + length - 1);
                    locationInfo.put(DEPENDENTLOCALITYNAME, text);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        super.endElement(uri, localName, qName);
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
    }

    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);
        //当存在<address>元素时
        if (localName.equals("address")) {
            hasAddress = true;
        } else if (localName.equals("CountryName")) {
            element = COUNTRYNAME;
        } else if (localName.equals("AdministrativeAreaName")) {
            element = ADMINISTRATIVEAREANAME;
        } else if (localName.equals("LocalityName")) {
            element = LOCALITYNAME;
        } else if (localName.equals("DependentLocalityName")) {
            element = DEPENDENTLOCALITYNAME;
        } else {
            element = 0;
        }
    }

    //判断是否存在地址
    public boolean hasAddress() {
        return hasAddress;
    }

    //得到详细的地址信息
    public Map<Integer, String> getDetailAddress() {
        return locationInfo;
    }
}