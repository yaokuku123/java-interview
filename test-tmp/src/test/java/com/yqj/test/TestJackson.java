package com.yqj.test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TestJackson {

    @Test
    public void test1() throws JsonProcessingException {
        //json字符串
        String strJson = "{\"gender\":\"男\",\"name\":\"张三\",\"age\":22}";
        //创建objectMapper对象
        ObjectMapper mapper = new ObjectMapper();
        //转换为person对象
        Person person = mapper.readValue(strJson, Person.class);
        //Person{name='张三', age=22, gender='男', birthday=null}
        System.out.println(person);
    }

    @Test
    public void test2() throws JsonProcessingException {
        String strJson = "[\n" +
                "                {\"name\":\"张三\",\"age\":22,\"gender\":\"男\",\"birthday\":null},\n" +
                "                {\"name\":\"李四\",\"age\":18,\"gender\":\"男\",\"birthday\":null},\n" +
                "                {\"name\":\"莉莉\",\"age\":15,\"gender\":\"女\",\"birthday\":null}\n" +
                "            ]";
        ObjectMapper mapper = new ObjectMapper();
        List<Person> peoples = mapper.readValue(strJson, new TypeReference<List<Person>>() {});
        for (Person people : peoples) {
            System.out.println(people);
        }
    }

    @Test
    public void test3() throws JsonProcessingException {
        String strJson = "{\"gender\":\"男\",\"name\":\"张三\",\"age\":22}";
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> map = mapper.readValue(strJson, new TypeReference<Map<String, Object>>() {});
        for (String key : map.keySet()) {
            System.out.println(key + ": " + map.get(key));
        }
    }
}
