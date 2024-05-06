# Go call using rust

## Where we spend energy?

We waste a lot of energy because we don't understand what it means to enter and exit scopes. Whenever you exit a function scope you move to another frame (calling a func) this includes methods, etc. Afterwards, you can find your memory footprint invoking the GC for the global count (300ms) etc.

## Where to use go, rust, c and other?

Go is more like a cloud networking language.
Languages close to hardware: Rust, C, C++, Zig, etc.,
then using things like wasm to make UIs more efficient.

## An article about using go with rust

Article by metal bear on attaching an assembly trampoline to translate Go calls into Rust calls.

- Go can handle concurrency
- all CPU intensive tasks could be handled by Rust before they are loaded onto the GC
- then Go handles all IO intensive tasks using Go routines.

Implementing package like this will help developers seamlessly pass through any green initiatives.

## Credits

1. Comment by [Sufficient_Ant_3008](https://www.reddit.com/r/golang/comments/1155vfx/comment/j94oys9/?utm_source=share&utm_medium=web3x&utm_name=web3xcss&utm_term=1&utm_content=share_button)
2. [Hooking Go from Rust - Hitchhiker’s Guide to the Go-laxy](https://metalbear.co/blog/hooking-go-from-rust-hitchhikers-guide-to-the-go-laxy/)

## Thank you

Thank you for contributing towards the green development 🌍.
