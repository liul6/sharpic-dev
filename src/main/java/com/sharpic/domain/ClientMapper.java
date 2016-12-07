package com.sharpic.domain;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ClientMapper {
    List<Client> getClients();
}