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

import org.eclipse.jetty.http.HttpStatus;
import org.magnum.dataup.model.Video;
import org.magnum.dataup.model.VideoStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import retrofit.http.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class VideoController {
    private static final AtomicLong currentId = new AtomicLong(0L);

    private Map<Long, Video> videos = new HashMap<>();

    public static final String DATA_PARAMETER = "data";

    public static final String ID_PARAMETER = "id";

    public static final String VIDEO_SVC_PATH = "/video";

    public static final String VIDEO_DATA_PATH = VIDEO_SVC_PATH + "/{id}/data";

    private static final String URL_BASE = "http://localhost:8080";
    private static final String VIDEO_URL = URL_BASE + VIDEO_SVC_PATH + "/%o/data";

    @GetMapping(VIDEO_SVC_PATH)
    public @ResponseBody
    Collection<Video> getVideoList() {
        return videos.values();
    }

    @PostMapping(VIDEO_SVC_PATH)
    public @ResponseBody
    Video addVideo(@RequestBody Video video) {
        try {
            video.setId(currentId.incrementAndGet());
            video.setDataUrl(String.format(VIDEO_URL, video.getId()));
            videos.put(video.getId(), video);
        } catch (Exception e) {
            e.getMessage();
        }
        return video;
    }

    @Multipart
    @PostMapping(VIDEO_DATA_PATH)
    public @ResponseBody
    VideoStatus setVideoData(@PathVariable(ID_PARAMETER) long id, @RequestParam(DATA_PARAMETER) MultipartFile videoData, HttpServletResponse response) throws IOException {
        Video video = videos.get(id);
        VideoStatus status = new VideoStatus(VideoStatus.VideoState.READY);

        if (video == null) {
            response.setStatus(HttpStatus.NOT_FOUND_404);
            return status;
        }

        VideoFileManager vfm = VideoFileManager.get();
        vfm.saveVideoData(video, videoData.getInputStream());
        return status;
    }

    @RequestMapping(VIDEO_DATA_PATH)
    public void getData(@PathVariable(ID_PARAMETER) long id, HttpServletResponse response) {
        try {
            VideoFileManager videoData = VideoFileManager.get();
            if(videos.get(id)==null) response.setStatus(HttpStatus.NOT_FOUND_404);
            else
                videoData.copyVideoData(videos.get(id), response.getOutputStream());
        } catch (Exception e) {
        }
    }
}
