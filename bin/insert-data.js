let axios = require('axios');

let url = "https://www.googleapis.com/youtube/v3/channels";
let key = "AIzaSyDEEwGOwUujh6rA6gWQnQRUw2-Uyfx1OOI";
let id = 'UCXIJgqnII2ZOINSWNOGFThA';

let fullUrl = `${url}?key=${key}&part=statistics&id=${id}`;

axios.get(fullUrl).then((response) => {
  let { subscriberCount, viewCount } = response.data.items[0].statistics;
});

