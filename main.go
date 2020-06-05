package main

import (
	"encoding/hex"
	"fmt"
	"io/ioutil"
	"os"
	"strings"

	"golang.org/x/crypto/ssh/terminal"
)

func main() {
	if !terminal.IsTerminal(0) {
		b, err := ioutil.ReadAll(os.Stdin)
		if err == nil {
			fmt.Println(string(b))
		}
		return
	}
	strs := os.Args
	hexStr := strs[1]
	if strings.HasPrefix(hexStr, "0x") {
		hexStr = hexStr[2:]
	}
	r, err := hex.DecodeString(hexStr)
	if err != nil {
		fmt.Println(err)
		return
	}
	fmt.Println(string(r))
}
