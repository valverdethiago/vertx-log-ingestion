## LAA - REST API

According to the problem statement three endpoints should be provided, here they are.

## 1. /laa/ingest
This endpoint only supports POST requests with the following format:

<code>
${url} ${timestamp} ${userId} ${regionId}
</code>

* url - It must be a valid relative url that will be parsed and if there's any id information like /pets/exotic/cat/10, the id should be replaced by {id}. This replacement is for avoiding multiple log entries of the same resource with different urls. Two access of the described url (cat) with different ids should be spoted with the same url
* timestamp - integer number with the correspondent timestamp for the access being logged.
* userId - A valid string with numbers, letters and dashes.
* regionId - A number between 1 and 3 representing the AWS region for the request.

## 2. /laa/health
This enpoint only supports GET requests and it returns a json with relevant information about the application. As this application needs connection to kafka and redis to work properly the endpoin will return json response as follows:

```json
{
    "checks": [
        {
            "id": "kafka-connection",
            "status": "UP"
        },
        {
            "id": "redis-connection",
            "status": "UP"
        }
    ],
    "outcome": "UP"
}
```

## 3. /laa/metrics
This endpoint only supports GET request and it returns a json object with metric according to the filter parameters:

### type 

It's a required path parameter that could contain the following values:
 * date - Brings the result grouped by the date depending on the other filter parameters 
 * region - Brings url ranking grouped by aws region
 * url - Brings the url ranking in general (whole world)
 * minute - Brings the minute ranking

### Searching ordered and limited results

Optional query parameters can be used to sort or limit the search results:
 * **order** - Optional parameter that can contain **top** (default value) or **bottom**. The last one reverts the original order
 * **size** - By default searchs returns all elements, but if we want to limit we can use this field to provide an integer value.

 ### Searching requirements:

 Based on the problem statement, there were 5 situations that must be well covered on the metrics API endpoint:

 * 1. Top 3 URL accessed all around the world
 ```
 http://localhost:8080/laa/metrics/url?order=top&size=3
 ```
```json
 {
    "rankingEntries": [
        {
            "key": "/pets/exotic/snake/{id}",
            "count": 1
        },
        {
            "key": "/pets/exotic/cat/{id}",
            "count": 1
        },
        {
            "key": "/pets/exotic/dog/{id}",
            "count": 1
        }
    ]
}
 ```

 * 2. Top 3 URL accessed PER region
 ```
 http://localhost:8080/laa/metrics/region?order=top&size=3
 ```
 ```json
{
    "groupedRankingEntries": [
        {
            "key": "us-east-1",
            "ranking": [
                {
                    "key": "/pets/exotic/cat/{id}",
                    "count": 1
                }
            ]
        },
        {
            "key": "ap-south-1",
            "ranking": [
                {
                    "key": "/pets/exotic/snake/{id}",
                    "count": 1
                }
            ]
        },
        {
            "key": "us-west-2",
            "ranking": [
                {
                    "key": "/pets/exotic/dog/{id}",
                    "count": 1
                }
            ]
        }
    ]
}
 ```
 * 3. The URL with less access in all world
 ```
 http://localhost:8080/laa/metrics/url?order=down&size=1
 ```
 ```json
 {
    "rankingEntries": [
        {
            "key": "/pets/exotic/dog/{id}",
            "count": 1
        }
    ]
}
 ```
 * 4. Top 3 Access per DAY, WEEK, YEAR (you recive the DAY/WEEK/YEAR by parameter)
 ```
 http://localhost:8080/laa/metrics/date?order=bottom&size=3?${filter}
 ```
 Where ${filter} is mandatory and can be:
  * day: day in the format of YYYY-MM-DD. Ex.: 2020-07-21
 ```json
 {
    "groupedRankingEntries": [
        {
            "key": "2020-07-23",
            "ranking": [
                {
                    "key": "/pets/exotic/snake/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/dog/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/cat/{id}",
                    "count": 1
                }
            ]
        }
    ]
}
 ```
  * week: week in the format of YYYY-dd. Ex.: 2020-35
 ```json
 {
    "groupedRankingEntries": [
        {
            "key": "2020-30",
            "ranking": [
                {
                    "key": "/pets/exotic/snake/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/dog/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/cat/{id}",
                    "count": 1
                }
            ]
        }
    ]
}
 ```
  * month: month in the format of YYYY-MM. Ex.: 2020-12
 ```json
 {
    "groupedRankingEntries": [
        {
            "key": "2020-07",
            "ranking": [
                {
                    "key": "/pets/exotic/snake/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/dog/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/cat/{id}",
                    "count": 1
                }
            ]
        }
    ]
}
 ```
  * year: year in the format of YYYY. Ex.: 2019
It's required to provide exactly one date filter to perform this query.
 ```json

 {
    "groupedRankingEntries": [
        {
            "key": "2020",
            "ranking": [
                {
                    "key": "/pets/exotic/snake/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/dog/{id}",
                    "count": 1
                },
                {
                    "key": "/pets/exotic/cat/{id}",
                    "count": 1
                }
            ]
        }
    ]
}
 ```
 * 5. The minute with more access in all URLs
 ```
 http://localhost:8080/laa/metrics/minute?order=top&size=1
 ```
```json
{
    "rankingEntries": [
        {
            "key": "2020-07-23 22-43",
            "count": 3
        }
    ]
}
```

## POSTMAN
The software used to test all the request is [postman](https://www.postman.com/) and the project can be dowloaded [here](./laa_postman_collection.json).