package com.vinylshop.dto;

import java.util.Map;

public interface Linkable {

    Map<String, Link> get_links();
    void set_links(Map<String, Link> _links);

}
