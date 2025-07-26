package com.vinylshop.advice;

import com.vinylshop.dto.Linkable;
import com.vinylshop.dto.SearchResponse;
import com.vinylshop.util.LinkProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
@RequiredArgsConstructor
public class LinkableResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    private final LinkProcessor linkProcessor;

    @Override
    public boolean supports(
        MethodParameter returnType,
        Class<? extends HttpMessageConverter<?>> converterType
    ) {
        ResolvableType type = ResolvableType.forMethodParameter(returnType);

        if (type.resolve() == ResponseEntity.class && type.getGeneric(0) != ResolvableType.NONE) {
            Class<?> genericClass = type.getGeneric(0).resolve();
            if (genericClass == null) {
                return false;
            }
            return Linkable.class.isAssignableFrom(genericClass)
                   || SearchResponse.class.isAssignableFrom(genericClass)
                   || Iterable.class.isAssignableFrom(genericClass);
        }

        Class<?> clazz = type.resolve();
        if (clazz == null) {
            return false;
        }
        return Linkable.class.isAssignableFrom(clazz)
               || SearchResponse.class.isAssignableFrom(clazz)
               || Iterable.class.isAssignableFrom(clazz);
    }


    @Override
    public Object beforeBodyWrite(
        Object body,
        MethodParameter returnType,
        MediaType contentType,
        Class<? extends HttpMessageConverter<?>> converterType,
        ServerHttpRequest request,
        ServerHttpResponse response
    ) {
        if (body instanceof Linkable linkable) {
            linkProcessor.processLinks(linkable);
        } else if (body instanceof SearchResponse searchResponse) {
            linkProcessAll(searchResponse.vinyls());
            linkProcessAll(searchResponse.giftCertificates());
        } else if (body instanceof Iterable<?> iterable) {
            linkProcessAll(iterable);
        }
        return body;
    }

    private void linkProcessAll(Iterable<?> iterable) {
        for(Object item : iterable) {
            if (item instanceof Linkable linkable) {
                linkProcessor.processLinks(linkable);
            }
        }
    }

}