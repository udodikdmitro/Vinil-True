package com.vinylshop.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Optional;
import java.util.stream.StreamSupport;

import static com.vinylshop.util.StringUtil.removeBrackets;

@Service
@RequiredArgsConstructor
public class DeezerService {

    private final RestTemplate restTemplate;

    @Value("${api.deezer.base-url}")
    private String deezerBaseUrl;

    public Optional<JsonNode> getAlbumObjectByAlbumAndArtist(String album, String artist) {
        if (album != null && artist != null)  {

            String normalizedAlbum = removeBrackets(album);
            String normalizedArtist = removeBrackets(artist);

            UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(deezerBaseUrl)
                .path("/search")
                .path("/album");

            uriBuilder.queryParam("q", createQuery(normalizedAlbum, normalizedArtist));

            String url = uriBuilder.build().toUriString();

            JsonNode jsonNode = restTemplate.getForObject(url, JsonNode.class);

            if (jsonNode != null && !jsonNode.isNull()) {
                JsonNode dataNode = jsonNode.get("data");
                if (dataNode != null && !jsonNode.isNull()) {
                    final String albumLower = normalizedAlbum.toLowerCase();
                    final String artistLower = normalizedArtist.toLowerCase();
                    return StreamSupport.stream(dataNode.spliterator(), false)
                        .filter(x -> {
                            Optional<String> albumTitle = Optional.ofNullable(x.get("title"))
                                .flatMap(title -> Optional.ofNullable(title.asText()))
                                .map(String::toLowerCase);
                            return albumTitle.isPresent() && (albumTitle.get().contains(albumLower) ||
                                                              albumLower.contains(albumTitle.get()));
                        })
                        .filter(x -> {
                            Optional<String> artistName = Optional.ofNullable(x.get("artist"))
                                .flatMap(a -> Optional.ofNullable(a.get("name")))
                                .flatMap(a -> Optional.ofNullable(a.asText()))
                                .map(String::toLowerCase);
                            return artistName.isPresent() && (artistName.get().contains(artistLower) ||
                                                              artistLower.contains(artistName.get()));
                        })
                        .limit(1)
                        .findFirst();
                }
            }
        }
        return Optional.empty();
    }

    public Optional<JsonNode> getAlbumObjectById(Long albumId) {
        return Optional.ofNullable(albumId).flatMap(x -> {
            String url = deezerBaseUrl + "/album/" + albumId;
            JsonNode jsonNode = restTemplate.getForObject(url, JsonNode.class);
            return Optional.ofNullable(jsonNode);
        });
    }

    private String createQuery(String album, String artist) {
        return removeBrackets(artist) + " " + removeBrackets(album);
    }

}
