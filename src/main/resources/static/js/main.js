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
        channel: response.data
      });
    });
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
        videos: response.data
      });
    });
  }

  render() {
    var videos = this.state.videos;

    return (
      <div className="videos container">
        <div className="row">
          {videos && videos.map(({ id, imgSrc, title, likeCount, dislikeCount, viewCount, publishedAt }) => {
            return (
              <div key={id} className="video col-12 col-sm-6 col-md-4 col-lg-3">
                <a href={`https://www.youtube.com/watch?v=${id}`} target="_blank">
                  <div className="video__img"><img src={imgSrc} /></div>
                  <div className="video__title">{title}</div>

                  <div className="video__info">
                      <span className="video__info-views">{viewCount} views</span>
                      <span className="video__info-spacer">&bull;</span>
                      <span className="video__info-likes">{likeCount} likes</span>
                      <span className="video__info-spacer">&bull;</span>
                      <span className="video__info-dislikes">{dislikeCount} dislikes</span>
                  </div>

                  <div className="video__info">{publishedAt}</div>
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


