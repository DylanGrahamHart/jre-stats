import React from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';

////

class App extends React.Component {
  render() {
    return (
      <div>
        <Channel />
        <Videos />
      </div>
    )
  }
}

class Channel extends React.Component {
  constructor(props) {
    super(props);

    axios.get('/channel').then((response) => {
      this.setState({channel: response.data});
    });
  }

  render() {
    try {
      var subscriberCount = this.state.channel.items[0].statistics.subscriberCount;
      var viewCount = this.state.channel.items[0].statistics.viewCount;
    } catch (e) {
      console.log('No channel');
    }

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

class Videos extends React.Component {
  constructor(props) {
    super(props);

    this.state = {videos: []};

    axios.get('/videos').then((response) => {
      this.setState({videos: response.data});
    });
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

  render() {
    var videos = this.state.videos.slice(0, 50);

    return (
      <div className="videos container">
        <div className="row">
          {videos && videos.map((video) => {
            const videoUrl = `https://www.youtube.com/watch?v=${video.id}`;
            const viewCount = this.formatNumber(video.viewCount);
            const likeCount = this.formatNumber(video.likeCount);
            const dislikeCount = this.formatNumber(video.dislikeCount);

            return (
              <div key={video.id} className="video col-12 col-sm-6 col-md-4 col-lg-3">
                <a href={videoUrl} target="_blank">
                  <div className="video__img"><img src={video.imgSrc} /></div>
                  <div className="video__title">{video.title}</div>

                  <div className="video__stats">
                    <span className="video__stats-views">
                      <span className="js-format-Number(number)">{viewCount}</span> views
                    </span>

                    <span style={{margin: "0 4px"}}>&bull;</span>

                    <span className="video__stats-likes">
                      <span className="js-format-number">{likeCount}</span> likes
                    </span>

                    <span style={{margin: "0 4px"}}>&bull;</span>

                    <span className="video__stats-dislikes">
                      <span className="js-format-number">{dislikeCount}</span> dislikes
                    </span>
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


