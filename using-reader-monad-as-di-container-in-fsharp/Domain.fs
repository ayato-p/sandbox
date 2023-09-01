namespace Demo.Domain

open System

type Author = { name: string }
type BookOverview = { title: string; author: Author }

type PublishDate = PublishDate of DateTime

type Book =
    | Published of BookOverview * PublishDate
    | Unpublished of BookOverview
