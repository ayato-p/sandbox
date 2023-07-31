namespace App.Usecases

open System

module FetchCompanyIds =
    let exec () : Async<int32 seq> = async { return seq { 1..1000 } }
