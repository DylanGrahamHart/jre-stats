import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';

////

function getUrlParams() {
  var urlParams = {};

  location.search.substr(1).split('&').forEach((param) => {
    var key = param.split('=')[0];
    var value = param.split('=')[1];

    urlParams[key] = value;
  });

  return urlParams;
}

////

class App extends React.Component {
  render() {
    return (
      <div>
        <Channel />
        <Controls />
        <Videos />
      </div>
    )
  }
}

class Channel extends React.Component {
  constructor(props) {
    super(props);

    this.state = {channel: {}};

    axios.get('/channel').then((response) => {
      this.setState({
        channel: this.parseChannelResponse(response.data)
      });
    });
  }

  parseChannelResponse(channel) {
    return {
      subscriberCount: channel.items[0].statistics.subscriberCount,
      viewCount: channel.items[0].statistics.viewCount
    }
  }

  render() {
    var {subscriberCount, viewCount} = this.state.channel;

    return (
      <div className="subs-views container">
        <div className="row">
          <div className="col-12">
            <div className="subs-views__inner">
                <div className="subs-views__stat"><b>Subscribers:</b> {subscriberCount}</div>
                <div className="subs-views__stat"><b>Views:</b> {viewCount}</div>
            </div>
          </div>
        </div>
      </div>
    )
  }
}


class Controls extends React.Component {
  constructor(props) {
    super(props);
    this.state = {value: getUrlParams().sortBy || 'publishedAt'};
    this.handleChange = this.handleChange.bind(this);
  }

  handleChange(event) {
    location.href = 'http://' + location.hostname + '?sortBy=' + event.target.value
  }

  render() {
    return (
      <div className="controls container">
        <div className="row">
          <div className="col-6 col-sm-3 col-md-1 controls__prev">
            <a>Prev</a>
          </div>

          <div className="col-6 col-sm-3 col-md-1 controls__next">
            <a>Next</a>
          </div>

          <div className="col-12 col-sm-6 col-md-10 controls__sort">
            <label>Sort By</label>

            <select value={this.state.value} onChange={this.handleChange}>
              <option value="publishedAt">Date added (newest)</option>
              <option value="-publishedAt">Date added (oldest)</option>
              <option value="viewCount">Views (most)</option>
              <option value="-viewCount">Views (least)</option>
              <option value="likeCount">Likes (most)</option>
              <option value="-likeCount">Likes (least)</option>
              <option value="dislikeCount">Dislikes (most)</option>
              <option value="-dislikeCount">Dislikes (least)</option>
              <option value="likesPerView">Likes per view (most)</option>
              <option value="-likesPerView">Likes per view (least)</option>
              <option value="dislikesPerView">Dislikes per view (most)</option>
              <option value="-dislikesPerView">Dislikes per view (least)</option>
            </select>
          </div>
        </div>
      </div>
    );
  }
}

class Videos extends React.Component {
  constructor(props) {
    super(props);

    this.state = {videos: []};

    axios.get('/videos').then((response) => {
      this.setState({
        videos: this.parseVideosResponse(response.data)
      });
    });
  }

  parseVideosResponse(videos) {
    return videos.map((video) => {
      var viewCount = Number(video.statistics.viewCount);
      var likeCount = Number(video.statistics.likeCount);
      var dislikeCount = Number(video.statistics.dislikeCount);
      var publishedAt = new Date(video.snippet.publishedAt);

      return {
        id: video.id,
        publishedAt: publishedAt.getTime(),
        publishedAtPretty: this.formatDate(publishedAt),
        title: video.snippet.title,
        url: `https://www.youtube.com/watch?v=${video.id}`,
        imgSrc: video.snippet.thumbnails.high.url,

        viewCount,
        viewCountPretty: this.formatNumber(video.statistics.viewCount),

        likeCount,
        likeCountPretty: this.formatNumber(video.statistics.likeCount),

        dislikeCount,
        dislikeCountPretty: this.formatNumber(video.statistics.dislikeCount),

        likesPerView: likeCount / viewCount,
        dislikesPerView: dislikeCount / viewCount,
      }
    });
  }

  formatDate(publishedAt) {
    var options = {month: 'short', year: 'numeric', day: 'numeric'};
    return publishedAt.toLocaleDateString('en-us', options);
  }

  formatNumber(number) {
    if (number.length > 9) {
      return (Number(number) / Math.pow(10, 9)).toFixed(1) + 'B';
    } else if (number.length > 6) {
      return (Number(number) / Math.pow(10, 6)).toFixed(1) + 'M';
    } else if (number.length > 3) {
      return (Number(number) / Math.pow(10, 3)).toFixed(0) + 'K';
    } else return Number(number);
  }

  getVideos() {
    var videos = this.state.videos;
    var { sortBy } = getUrlParams();

    if (sortBy) {
      var reverseFlag = sortBy.indexOf('-') !== -1;

      if (reverseFlag)
          sortBy = sortBy.substr(1);

      videos.sort((video1, video2) => {
        return (video2[sortBy] - video1[sortBy]) * (reverseFlag ? -1 : 1);
      });
    }

    return this.state.videos.slice(0, 50);
  }

  render() {
    var videos = this.getVideos();

    return (
      <div className="videos container">
        <div className="row">
          {videos && videos.map(({
            id, publishedAtPretty, url, imgSrc, title, viewCountPretty, likeCountPretty, dislikeCountPretty
          }) => {
            return (
              <div key={id} className="video col-12 col-sm-6 col-md-4 col-lg-3">
                <a href={url} target="_blank">
                  <div className="video__img"><img src={imgSrc} /></div>
                  <div className="video__title">{title}</div>

                  <div className="video__info">
                      <span className="video__info-views">{viewCountPretty} views</span>
                      <span className="video__info-spacer">&bull;</span>
                      <span className="video__info-likes">{likeCountPretty} likes</span>
                      <span className="video__info-spacer">&bull;</span>
                      <span className="video__info-dislikes">{dislikeCountPretty} dislikes</span>
                  </div>

                  <div className="video__info">
                    {publishedAtPretty}
                  </div>
                </a>
              </div>
            )
          })}
        </div>
      </div>

    )
  }
}

////

ReactDOM.render(
  <App />,
  document.getElementById('app-root')
);


