package com.pji.triagem.base.service;

import com.pji.triagem.base.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

public interface ResponseService {

    /**
     * Método responsável por criar uma resposta com status 201
     * @param data - dados a serem retornados
     * @return - resposta com status 201
     * @param <T> - tipo do dado a ser retornado
     */
    <T> ResponseEntity<ResponseDTO<T>> created(T data);

    /**
     * Método responsável por criar uma resposta com status 200
     * @param data - dados a serem retornados
     * @return - resposta com status 200
     * @param <T> - tipo do dado a ser retornado
     */
    <T> ResponseEntity<ResponseDTO<T>> ok(T data);


    /**
     * Método responsavel por criar uma resposta com status 200
     * @param response - resposta a ser retornada
     * @return - resposta com status 200
     * @param  - tipo do dado a ser retornado
     */
    ResponseEntity<ResponseDTO<String>> ok(String response);

    /**
     * Método responsável por criar uma resposta com status 404
     * @return - resposta com status 404
     * @param <T> - tipo do dado a ser retornado
     */
    <T> ResponseEntity<T> notFound();

}
