package com.wujk.mybatis.db;

public interface MapperInterface {

   public <M> M getMapper(Class<M> clazz, String dataBaseId);
}
