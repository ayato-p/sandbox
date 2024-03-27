namespace file_upload
#nowarn "20"
open System
open System.Collections.Generic
open System.IO
open System.Linq
open System.Threading.Tasks
open Microsoft.AspNetCore
open Microsoft.AspNetCore.Builder
open Microsoft.AspNetCore.Hosting
open Microsoft.AspNetCore.HttpsPolicy
open Microsoft.AspNetCore.Http
open Microsoft.Extensions.Configuration
open Microsoft.Extensions.DependencyInjection
open Microsoft.Extensions.Hosting
open Microsoft.Extensions.Logging

module Program =
    let exitCode = 0

    [<EntryPoint>]
    let main args =

        let builder = WebApplication.CreateBuilder(args)
        let app = builder.Build()

        app.MapPost("/upload", Func<IFormFileCollection, Task<IResult>>(fun (files) -> 
            async {
                let tempfile = "/tmp/ayato-p-copy.png"
                use stream = File.OpenWrite(tempfile)
                do files
                    |> Seq.head
                    |> (fun x -> x.CopyTo(stream)) 

                return Results.Ok()
            } |> Async.StartAsTask
        )).DisableAntiforgery().Accepts<IFormFile>("multipart/form-data")

        app.Run()

        exitCode
