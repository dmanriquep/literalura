package com.aluracursos.literalura.service;

public interface IConvertirDatos {
    <T> T getData(String json, Class<T> classes);
}
