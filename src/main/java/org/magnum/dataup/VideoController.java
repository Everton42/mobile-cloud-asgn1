/*
 *
 * Copyright 2014 Jules White
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magnum.dataup;

import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.web.bind.annotation.*;
import retrofit.client.Response;
import retrofit.http.*;
import retrofit.mime.TypedFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class VideoController {
	private static final AtomicLong currentId = new AtomicLong(0L);

	private Map<Long,Video> videos = new HashMap<>();

	public static final String DATA_PARAMETER = "data";

	public static final String ID_PARAMETER = "id";

	public static final String VIDEO_SVC_PATH = "/video";

	public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";

	private static final String URL_BASE = "http://localhost:8080";
	private static final String VIDEO_URL = URL_BASE + VIDEO_SVC_PATH + "/%o/data";

	@GetMapping(VIDEO_SVC_PATH)
	public Collection<Video> getVideoList() {
		return null;
	}

	@PostMapping(VIDEO_SVC_PATH)
	public Video addVideo(@RequestBody Video video) {
		try {
			video.setId(currentId.incrementAndGet());
			video.setDataUrl(String.format(VIDEO_URL, video.getId()));
			videos.put(video.getId(), video);
  			return video;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Multipart
	@PostMapping(VIDEO_DATA_PATH)
	public VideoStatus setVideoData(@Path(ID_PARAMETER) long id, @Part(DATA_PARAMETER) TypedFile videoData) {
		return null;
	}

	@Streaming
	@GetMapping(VIDEO_DATA_PATH)
	public Response getData(long id) {
		return null;
	}
}
