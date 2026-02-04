package org.adpia.official.domain.link.service;

import java.io.IOException;
import java.net.*;
import java.time.Duration;
import java.util.Locale;

import org.adpia.official.dto.link.LinkPreviewResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class LinkPreviewService {

	private static final int TIMEOUT_MS = (int) Duration.ofSeconds(4).toMillis();
	private static final int MAX_BODY_SIZE_BYTES = 1_000_000; // 1MB

	public LinkPreviewResponse preview(String rawUrl) {
		String normalized = normalizeUrl(rawUrl);
		validateUrlForSSRF(normalized);

		try {
			Document doc = Jsoup.connect(normalized)
				.userAgent("ADPIA-LinkPreviewBot/1.0")
				.timeout(TIMEOUT_MS)
				.maxBodySize(MAX_BODY_SIZE_BYTES)
				.followRedirects(true)
				.get();

			String ogTitle = contentOf(doc, "meta[property=og:title]");
			String ogDesc = contentOf(doc, "meta[property=og:description]");
			String ogImage = contentOf(doc, "meta[property=og:image]");
			String ogSiteName = contentOf(doc, "meta[property=og:site_name]");

			String title = firstNonBlank(ogTitle, doc.title(), hostname(normalized));
			String desc = firstNonBlank(ogDesc, contentOf(doc, "meta[name=description]"));
			String siteName = firstNonBlank(ogSiteName, hostname(normalized));

			String image = absolutize(normalized, ogImage);

			return LinkPreviewResponse.builder()
				.url(normalized)
				.siteName(siteName)
				.title(title)
				.desc(desc)
				.image(image)
				.build();

		} catch (IOException e) {
			return LinkPreviewResponse.builder()
				.url(normalized)
				.siteName(hostname(normalized))
				.title(hostname(normalized))
				.desc(null)
				.image(null)
				.build();
		}
	}

	private String contentOf(Document doc, String cssQuery) {
		Element el = doc.selectFirst(cssQuery);
		if (el == null) return null;
		String v = el.attr("content");
		return v != null && !v.isBlank() ? v.trim() : null;
	}

	private String firstNonBlank(String... arr) {
		for (String s : arr) {
			if (s != null && !s.isBlank()) return s.trim();
		}
		return null;
	}

	private String normalizeUrl(String raw) {
		if (raw == null) throw new IllegalArgumentException("url이 필요합니다.");
		String v = raw.trim();
		if (v.isEmpty()) throw new IllegalArgumentException("url이 필요합니다.");

		if (v.startsWith("//")) v = "https:" + v;

		String lower = v.toLowerCase(Locale.ROOT);
		if (!lower.startsWith("http://") && !lower.startsWith("https://")) {
			v = "https://" + v;
		}
		return v;
	}

	private void validateUrlForSSRF(String url) {
		try {
			URI uri = URI.create(url);

			String scheme = uri.getScheme();
			if (!"http".equalsIgnoreCase(scheme) && !"https".equalsIgnoreCase(scheme)) {
				throw new IllegalArgumentException("http/https 링크만 허용됩니다.");
			}

			String host = uri.getHost();
			if (host == null || host.isBlank()) {
				throw new IllegalArgumentException("유효하지 않은 url입니다.");
			}

			InetAddress addr = InetAddress.getByName(host);

			if (addr.isAnyLocalAddress()
				|| addr.isLoopbackAddress()
				|| addr.isLinkLocalAddress()
				|| addr.isSiteLocalAddress()
				|| isPrivateRange(addr)) {
				throw new IllegalArgumentException("허용되지 않은 주소입니다.");
			}

		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			throw new IllegalArgumentException("유효하지 않은 url입니다.");
		}
	}

	private boolean isPrivateRange(InetAddress addr) {
		byte[] ip = addr.getAddress();
		if (ip.length != 4) return false;

		int b0 = ip[0] & 0xFF;
		int b1 = ip[1] & 0xFF;

		if (b0 == 10) return true;
		if (b0 == 172 && (b1 >= 16 && b1 <= 31)) return true;
		if (b0 == 192 && b1 == 168) return true;

		return false;
	}

	private String hostname(String url) {
		try {
			return new URL(url).getHost().replaceFirst("^www\\.", "");
		} catch (Exception e) {
			return url;
		}
	}

	private String absolutize(String base, String maybeRelative) {
		if (maybeRelative == null || maybeRelative.isBlank()) return null;
		try {
			URI b = URI.create(base);
			URI resolved = b.resolve(maybeRelative);
			return resolved.toString();
		} catch (Exception e) {
			return maybeRelative;
		}
	}
}
