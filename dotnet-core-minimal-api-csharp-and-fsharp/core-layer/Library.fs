namespace Demoapp.MyGreatUsecase

open System.Threading.Tasks

module EchoUsecase = 
    let execute (input: string): Task<Result<string, string>> = 
        task {
            if input.Length = 0 then 
                return Error "Input is empty"
            else
                return Ok input
        }