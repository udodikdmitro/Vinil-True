package com.vinylshop.util;

import com.vinylshop.dto.Link;
import com.vinylshop.dto.Linkable;
import com.vinylshop.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class LinkProcessor {

    public static final String LINKS_SELF_NAME = "self";

    @Value("${api.context-path:/api}")
    private String contextPath;

    @Value("${api.version:/v1}")
    private String apiVersion;

    public void processLinks(Linkable linkable) {
        Objects.requireNonNull(linkable);

        Map<String, Link> _links = linkable.get_links();
        if (_links == null) {
            _links = new HashMap<>();
            linkable.set_links(_links);
        }

        UriComponentsBuilder baseBuilder = ServletUriComponentsBuilder.fromCurrentContextPath()
            .path(contextPath)
            .path(apiVersion);

        String self = getSelfLink(linkable, baseBuilder);
        putLink(_links, LINKS_SELF_NAME, self);
    }

    public String getSelfLink(Linkable linkable, UriComponentsBuilder baseBuilder) {
        if (linkable instanceof ProductDto productDto) {
            UriComponentsBuilder resourceUriBuilder = switch (productDto.getType()) {
                case VINYL -> baseBuilder.cloneBuilder().path("/vinyls");
                case GIFT_CERTIFICATE -> baseBuilder.cloneBuilder().path("/gift-certificates");
            };
            resourceUriBuilder.path("/{id}");
            return resourceUriBuilder.buildAndExpand(productDto.getId()).toUriString();
        }
        return null;
    }

    private void putLink(Map<String, Link> _links, String name, String link) {
        if (link != null) {
            _links.put(name, new Link(link));
        }
    }

}
