import { useState } from "react";
import "../css/YoutubeVideo.css";
import { FormattedMessage } from "react-intl";

const YoutubeVideo = ({ youtubeVideoId }) => {
  const [open, setOpen] = useState(false);

  if (!youtubeVideoId) return null;

  const url = `https://www.youtube.com/embed/${youtubeVideoId}`;

  return (
    <div className="youtube-wrapper">
      <button className="toggle-button" type="button" onClick={() => setOpen(!open)}>
        {open ? (
          <FormattedMessage id="project.exercises.video.hide" />
        ) : (
          <FormattedMessage id="project.exercises.video.show" />
        )}
      </button>

      <div className={`video-container ${open ? "open" : ""}`}>
        <iframe
          src={url}
          title="YouTube video player"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowFullScreen
        />
      </div>
    </div>
  );
};

export default YoutubeVideo;
