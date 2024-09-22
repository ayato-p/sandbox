namespace DemoApp


open FSharpPlus
open FSharpPlus.Control
open Npgsql

type Mood =
    | Sad = 0
    | Ok =1
    | Happy =2

type ReportTypeId' = ReportTypeId' of uint

type ReportTypeId =
    | Supported of ReportTypeId'
    | Unsupported of ReportTypeId'

module ReportTypeId =
  let ofReportTypeId: uint -> ReportTypeId =
    let juxtApply f g x = (f x) (g x)
    juxtApply
         (function
          | 200u -> Supported
          | _ -> Unsupported)
         ReportTypeId' 

module CLI = 
    let connectionString = "Host=localhost;Username=acmeuser;Password=password;Port=5433;Database=acme"

    [<EntryPoint>]
    let main args = 
        let datasourceBuilder = NpgsqlDataSourceBuilder(connectionString)
        datasourceBuilder.MapEnum<Mood>() |> ignore
        use datasource = datasourceBuilder.Build()
        // plain npgsql
        task {
            use! conn = datasource.OpenConnectionAsync()

            // write
            let command = new NpgsqlCommand("insert into acme.person (name, current_mood) values (@name, @mood)", conn)
            command.Parameters.Add({ new NpgsqlParameter(ParameterName = "name") with
                                        member _.Value = "Taro" }) |> ignore
            command.Parameters.Add({ new NpgsqlParameter(ParameterName = "mood") with 
                                        member _.Value = Mood.Ok }) |> ignore
            let! _ = command.ExecuteNonQueryAsync() 

            // read
            let command = new NpgsqlCommand("select name, current_mood from acme.person", conn)
            use! reader = command.ExecuteReaderAsync()
            do! reader.ReadAsync() |> Async.AwaitTask |> Async.Ignore
            let name = reader.GetString(0)
            let mood = reader.GetValue(1) :?> Mood
            do printfn "name: %s, mood: %A" name mood
            return ()
        } |> Async.AwaitTask |> Async.RunSynchronously

        0