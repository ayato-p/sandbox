namespace App.Driver

open System.Net.Http

type ExampleDriver1(client: HttpClient) =
    member _.client = client

type ExampleDriver2(client: HttpClient) =
    member _.client = client
