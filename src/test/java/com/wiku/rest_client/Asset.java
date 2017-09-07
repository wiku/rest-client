package com.wiku.rest_client;

import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
public class Asset
{

    private int id;
    private String name;
    private String type;
    private int amount;
    private int worth;
    private String identifier;

}
