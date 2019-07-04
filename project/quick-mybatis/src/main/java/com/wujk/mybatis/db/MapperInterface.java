package com.wujk.mybatis.db;

public interface MapperInterface<M> {

   public M getMapper(Class<M> clazz, String dataBaseId);
}
