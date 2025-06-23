#ifndef CALC_ICE
#define CALC_ICE

module PrinterModule{

    sequence<string> StringSeq;
    exception GenericError {
        string reason;
    };
    exception InvalidCountError extends GenericError {};
    interface Printer {
        StringSeq printDuplicated(StringSeq strings, int count) throws InvalidCountError;
        StringSeq printRange(int startNum, int endNum, int step);
        StringSeq printFibonacci(int count) throws InvalidCountError;
        idempotent string getDescription();
        idempotent int getPrintedStringsNum();
    };
};

#endif