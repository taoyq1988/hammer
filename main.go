package main

import (
	"encoding/hex"
	"flag"
	"fmt"
	"golang.org/x/crypto/ssh/terminal"
	"io/ioutil"
	"os"
	"strconv"
	"strings"
)

var version = "0.0.1"

// Options will be used when parsing cmd flags
type Options struct {
	HelpFlag    bool
	Version     bool
	EncodeNum   bool
	DecodeNum   bool
}

// Parse is used to parse cmd flags into Options
func Parse() (Options, []string) {
	options := Options{}
	flag.BoolVar(&options.HelpFlag, "help", false, "Displays usage information and exit.")
	flag.BoolVar(&options.Version, "version", false, "hex tool version.")
	flag.BoolVar(&options.EncodeNum, "en", false, "encode number to hex")
	flag.BoolVar(&options.DecodeNum, "dn", false, "decode a hex number")
	flag.Parse()
	return options, flag.Args()
}

func main() {
	if !terminal.IsTerminal(0) {
		b, err := ioutil.ReadAll(os.Stdin)
		if err != nil {
			fmt.Println(err.Error())
			return
		}
		fmt.Println(decodeHexString(string(b)))
		return
	}

	opts, args := Parse()
	switch {
	case opts.HelpFlag:
		flag.PrintDefaults()
	case opts.Version:
		fmt.Println("version", version)
	case opts.EncodeNum:
		fmt.Println(encodeNum(args[0]))
	case opts.DecodeNum:
		fmt.Println(decodeNum(args[0]))
	default:
		fmt.Println(decodeHexString(args[0]))
	}
}

func encodeNum(n string) string {
	i, err := strconv.ParseInt(n, 10, 64)
	if err != nil {
		return err.Error()
	}
	return strconv.FormatInt(i, 16)
}

func decodeNum(n string) string {
	i, err := strconv.ParseInt(n, 16, 64)
	if err != nil {
		return err.Error()
	}
	return strconv.FormatInt(i, 10)
}

func decodeHexString(hexStr string) string {
	if strings.HasPrefix(hexStr, "0x") {
		hexStr = hexStr[2:]
	}
	hexStr = strings.TrimSpace(hexStr)
	r, err := hex.DecodeString(hexStr)
	if err != nil {
		return err.Error()
	}
	return string(r)
}
