#include <SImage.h>
#include <SImageIO.h>
#include <cmath>
#include <algorithm>
#include <iostream>
#include <fstream>
#include <vector>
#include <DrawText.h>
#include <limits>
#include <list>
#include <stdlib.h>
#include <limits>

using namespace std;

// The simple image class is called SDoublePlane, with each pixel represented as
// a double (floating point) type. This means that an SDoublePlane can represent
// values outside the range 0-255, and thus can represent squared gradient magnitudes,
// harris corner scores, etc.
//
// The SImageIO class supports reading and writing PNG files. It will read in
// a color PNG file, convert it to grayscale, and then return it to you in
// an SDoublePlane. The values in this SDoublePlane will be in the range [0,255].
//
// To write out an image, call write_png_file(). It takes three separate planes,
// one for each primary color (red, green, blue). To write a grayscale image,
// just pass the same SDoublePlane for all 3 planes. In order to get sensible
// results, the values in the SDoublePlane should be in the range [0,255].
//

// Below is a helper functions that overlays rectangles
// on an image plane for visualization purpose.

// Draws a rectangle on an image plane, using the specified gray level value and line width.
//
void overlay_rectangle(SDoublePlane &input, int _top, int _left, int _bottom, int _right, double graylevel, int width)
{
    for(int w=-width/2; w<=width/2; w++) {
        int top = _top+w, left = _left+w, right=_right+w, bottom=_bottom+w;
        
        // if any of the coordinates are out-of-bounds, truncate them
        top = min( max( top, 0 ), input.rows()-1);
        bottom = min( max( bottom, 0 ), input.rows()-1);
        left = min( max( left, 0 ), input.cols()-1);
        right = min( max( right, 0 ), input.cols()-1);
        
        // draw top and bottom lines
        for(int j=left; j<=right; j++)
            input[top][j] = input[bottom][j] = graylevel;
        // draw left and right lines
        for(int i=top; i<=bottom; i++)
            input[i][left] = input[i][right] = graylevel;
    }
}

// DetectedSymbol class may be helpful!
//  Feel free to modify.
//
typedef enum {NOTEHEAD=0, QUARTERREST=1, EIGHTHREST=2} Type;
class DetectedSymbol {
public:
    int row, col, width, height;
    Type type;
    char pitch;
    double confidence;
};

struct line {
    int row;
    char pitch;
    //bool operator<(line const& A, line const& B) { return A.row < B.row; }
};

// Function that outputs the ascii detection output file
void  write_detection_txt(const string &filename, const vector<struct DetectedSymbol> &symbols)
{
    ofstream ofs(filename.c_str());
    
    for(int i=0; i<symbols.size(); i++)
    {
        const DetectedSymbol &s = symbols[i];
        ofs << s.row << " " << s.col << " " << s.width << " " << s.height << " ";
        if(s.type == NOTEHEAD)
            ofs << "filled_note " << s.pitch;
        else if(s.type == EIGHTHREST)
            ofs << "eighth_rest _";
        else
            ofs << "quarter_rest _";
        ofs << " " << s.confidence << endl;
    }
}

// Function that outputs a visualization of detected symbols
void  write_detection_image(const string &filename, const vector<DetectedSymbol> &symbols, const SDoublePlane &input)
{
    SDoublePlane output_planes[3];
    for(int i=0; i<3; i++)
        output_planes[i] = input;
    
    for(int i=0; i<symbols.size(); i++)
    {
        const DetectedSymbol &s = symbols[i];
        
        overlay_rectangle(output_planes[s.type], s.row, s.col, s.row+s.height-1, s.col+s.width-1, 255, 2);
        overlay_rectangle(output_planes[(s.type+1) % 3], s.row, s.col, s.row+s.height-1, s.col+s.width-1, 0, 2);
        overlay_rectangle(output_planes[(s.type+2) % 3], s.row, s.col, s.row+s.height-1, s.col+s.width-1, 0, 2);
        
        if(s.type == NOTEHEAD)
        {
            char str[] = {s.pitch, 0};
            draw_text(output_planes[0], str, s.row, s.col+s.width+1, 0, 2);
            draw_text(output_planes[1], str, s.row, s.col+s.width+1, 0, 2);
            draw_text(output_planes[2], str, s.row, s.col+s.width+1, 0, 2);
        }
    }
    
    SImageIO::write_png_file(filename.c_str(), output_planes[0], output_planes[1], output_planes[2]);
}


// The rest of these functions are incomplete. These are just suggestions to
// get you started -- feel free to add extra functions, change function
// parameters, etc.

// Convolve an image with a separable convolution kernel
//
SDoublePlane convolve_separable(const SDoublePlane &input, const SDoublePlane &row_filter, const SDoublePlane &col_filter)
{
    SDoublePlane output(input.rows(), input.cols());
    int k = (row_filter.rows()-1)/2;
    for(int i=0; i<input.rows(); i++)
    {
        for(int j=0; j<input.cols(); j++)
        {
            for(int l=-k; l<=k; l++)
            {
                if(j-l >= 0 && j-l < input.cols())
                {
                    output[i][j]=output[i][j] + row_filter[l+k][0]*input[i][j-l];
                }
                else
                {
                    output[i][j]=output[i][j] + 0;
                }
            }
        }
    }
    
    SDoublePlane output1(input.rows(), input.cols());
    for(int i=0; i<input.rows(); i++)
    {
        for(int j=0; j<input.cols(); j++)
        {
            for(int l=-k; l<=k; l++)
            {
                if(i-l >= 0 && i-l < input.rows())
                {
                    output1[i][j]= output1[i][j] + col_filter[l+k][0]*output[i-l][j];
                }
                else
                {
                    output1[i][j]= output1[i][j] + 0;
                }
            }
        }
    }
    return output1;
}




// Convolve an image with a kernel
//
SDoublePlane convolve_general(const SDoublePlane &input, const SDoublePlane &filter)
{
    SDoublePlane output(input.rows(), input.cols());
    int k = (filter.rows()-1)/2;
    int l = (filter.cols()-1)/2;
    for(int i=0; i<input.rows(); i++)
    {
        for(int j=0; j<input.cols(); j++)
        {
            for(int m=-k; m<=k; m++)
            {
                for(int n=-l; n<=l; n++)
                {
                    if(i-m >= 0 && i-m < input.rows() && j-n >=0 && j-n < input.cols())
                    {
                        output[i][j]= output[i][j] + filter[m+k][n+l]*input[i-m][j-n];
                    }
                    else
                    {
                        output[i][j]= output[i][j] + 0;
                    }
                }
            }
        }
    }
    return output;
}


// Apply a sobel operator to an image, returns the result
//
SDoublePlane sobel_gradient_filter(const SDoublePlane &input)//, bool _gx)
{
    SDoublePlane outputgx(input.rows(), input.cols());
    SDoublePlane outputgy(input.rows(), input.cols());
    SDoublePlane output(input.rows(), input.cols());
    SDoublePlane filterA(3,3);
    SDoublePlane filterB(3,3);
    filterA[0][0] = 1;
    filterA[1][0] = 2;
    filterA[2][0] = 1;
    filterB[0][0] = 1;
    filterB[1][0] = 0;
    filterB[2][0] = -1;
    
    outputgx = convolve_separable(input, filterB, filterA);
    outputgy = convolve_separable(input, filterA, filterB);
    
    for(int i=0; i<output.rows(); i++)
    {
        for(int j=0; j<output.cols(); j++)
        {
            output[i][j] =  sqrt (pow(outputgx[i][j], 2) + pow(outputgy[i][j], 2));
        }
    }
    return output;
}

SDoublePlane reduceNoiseFilter(const SDoublePlane &input)
{
    SDoublePlane output(input.rows(), input.cols());
    SDoublePlane filter(5,5);
    /*for (int i=0;i<3;i++){
     for (int j=0;j<3;j++){
     filter[i][j]=1;
     }
     }*/
    filter[0][0] = 0.003;
    filter[0][1] = 0.013;
    filter[0][2] = 0.022;
    filter[0][3] = 0.013;
    filter[0][4] = 0.003;
    filter[1][0] = 0.013;
    filter[1][1] = 0.059;
    filter[1][2] = 0.097;
    filter[1][3] = 0.059;
    filter[1][4] = 0.013;
    filter[2][0] = 0.022;
    filter[2][1] = 0.097;
    filter[2][2] = 0.159;
    filter[2][3] = 0.097;
    filter[2][4] = 0.022;
    filter[3][0] = 0.013;
    filter[3][1] = 0.059;
    filter[3][2] = 0.097;
    filter[3][3] = 0.059;
    filter[3][4] = 0.013;
    filter[4][0] = 0.003;
    filter[4][1] = 0.013;
    filter[4][2] = 0.022;
    filter[4][3] = 0.013;
    filter[4][4] = 0.003;
    
    output = convolve_general(input, filter);
    
    return output;
}

SDoublePlane edge(const SDoublePlane &input)
{
    SDoublePlane output(input.rows(), input.cols());
    SDoublePlane filter(3,3);
    /*for (int i=0;i<3;i++){
     for (int j=0;j<3;j++){
     filter[i][j]=1;
     }
     }*/
    filter[0][0] = 0.125;
    filter[0][1] = 0.25;
    filter[0][2] = 0.125;
    filter[1][0] = 0;
    filter[1][1] = 0;
    filter[1][2] = 0;
    filter[2][0] = -0.125;
    filter[2][1] = -0.25;
    filter[2][2] = -0.125;
    
    output = convolve_general(input, filter);
    
    return output;
}

// Apply an edge detector to an image, returns the binary edge map
//
SDoublePlane find_edges(const SDoublePlane &input, double thresh=0)
{
    SDoublePlane output(input.rows(), input.cols());
    
    // Implement an edge detector of your choice, e.g.
    // use your sobel gradient operator to compute the gradient magnitude and threshold
    
    return output;
}

char line2pitch(int line)
{
    line = line%10;
    switch(line){
        case 0:
            return 'F';
        case 1:
            return 'D';
        case 2:
            return 'B';
        case 3:
            return 'G';
        case 4:
            return 'E';
        case 5:
            return 'A';
        case 6:
            return 'F';
        case 7:
            return 'D';
        case 8:
            return 'B';
        case 9:
            return 'G';
        default:
            return 'A';
    }
}

vector<line> line_detect(SDoublePlane &input)
{
    SDoublePlane hedge = edge(input);
    SImageIO::write_png_file("horizontaledges.png", hedge,hedge,hedge);
    
    vector<line> lines;
    
    SDoublePlane output_planes[3];
    for(int i=0; i<3; i++)
        output_planes[i] = input;
    
    int count = 0;
    
    for (int i=0; i<hedge.rows(); i++) {
        for (int j=0; j<hedge.cols(); j++) {
            hedge[i][j] = 127+hedge[i][j];
        }
    }
    
    for (int i=0; i<hedge.rows(); i++) {
        int sum = 0;
        for (int j=0; j<hedge.cols(); j++) {
            sum += hedge[i][j];
        }
        
        if (sum < 200000 && i>5 && i<hedge.rows()-5){
            if (count>=1){
                if (i-lines[count-1].row>5){
                    line l;
                    l.row = i;
                    l.pitch = line2pitch(count);
                    lines.push_back(l);
                    count++;
                }
            }
            else {
                line l;
                l.row = i;
                l.pitch = line2pitch(count);
                lines.push_back(l);
                count++;
            }
        }
        
        //if (sum < min) {
        //    min = sum;
        //    min_row = i;
        //}
    }
    
    for (int i = 0; i<lines.size();i++){
        //cout << mins[i] << "\n";
        for (int j = 0; j<input.cols();j++) {
            output_planes[2][lines[i].row][j] = 255;
            output_planes[0][lines[i].row][j] = 0;
            output_planes[1][lines[i].row][j] = 0;
        }
    }
    
    SImageIO::write_png_file("staves.png", output_planes[0], output_planes[1], output_planes[2]);
    
    int interval = lines[4].row - lines[3].row;
    
    line l1;
    l1.row = lines[4].row + interval;
    l1.pitch = 'C';
    lines.push_back(l1);
    
    line l2;
    l2.row = lines[5].row - interval;
    l2.pitch = 'C';
    lines.push_back(l2);
    
    //return mins;
    return lines;
}



//Calculates the similarity score based on hamming distance
SDoublePlane similarityScores(const SDoublePlane &input, const SDoublePlane &templateImage)
{
    SDoublePlane simScores(input.rows() - templateImage.rows() + 1, input.cols() - templateImage.cols() + 1);
    for(int i=0; i<input.rows() - templateImage.rows() + 1; i++)
    {
        for(int j=0; j<input.cols() - templateImage.cols() + 1; j++)
        {
            for(int m=0; m<templateImage.rows(); m++)
            {
                for(int n=0; n<templateImage.cols(); n++)
                {
                    simScores[i][j]= simScores[i][j] + templateImage[m][n]*input[i+m][j+n] + (1-input[i+m][j+n])*(1-templateImage[m][n]);
                }
            }
        }
    }
    return simScores;
}


//Given a threshold, it makes values less than threshold 1 (for black) and those greater than threshold to 0 (for white)
SDoublePlane convertToBinary(const SDoublePlane &input, int threshold){
    SDoublePlane output(input.rows(), input.cols());
    for(int i=0; i<input.rows(); i++)
    {
        for(int j=0; j<input.cols(); j++)
        {
            if(input[i][j] >= threshold){
                output[i][j] = 0;
            }
            else{
                output[i][j] = 1;
            }
        }
    }
    return output;
}

char pitch_detect(vector<line> lines, int row)
{
    int interval = lines[4].row - lines[3].row;
    int min = numeric_limits<int>::max();
    int goal;
    for (int i = 0; i<lines.size(); i++){
        int dis = abs(row - lines[i].row);
        if (dis<min){
            min = dis;
            goal = i;
        }
    }
    if (min < (interval/3))
        return lines[goal].pitch;
    else
    {
        if (row - lines[goal].row > 0) //it is lower goal line
        {
            if (lines[goal].pitch == 'A')
                return 'G';
            else return lines[goal].pitch-1;
        }
        else //it is upper goal line
        {
            if (lines[goal].pitch == 'G')
                return 'A';
            else return lines[goal].pitch+1;
        }
    }
    
}


//detects a given template in the image with similarity Scores using hamming distance, given a percentage threshold on the score and threshold to convert the scores to binary
vector<DetectedSymbol> detectTemplate(const SDoublePlane &input_image,const SDoublePlane &template_image, int greyScaleThresh, vector<DetectedSymbol> symbols, float scoreThreshold, int type, vector<line> v){
    SDoublePlane template_result = similarityScores(convertToBinary(input_image, greyScaleThresh), convertToBinary(template_image, greyScaleThresh));
    
    double max = 0;
    for(int i = 0; i < template_result.rows(); i++){
        for(int j = 0; j < template_result.cols(); j++){
            if(template_result[i][j] > max){
                max = template_result[i][j];
            }
        }
    }
    
    for(int i = 0; i < template_result.rows(); i++){
        for(int j = 0; j < template_result.cols(); j++){
            if(template_result[i][j] > scoreThreshold * max){
                DetectedSymbol s;
                s.row = i;
                s.col = j;
                s.width = template_image.cols();
                s.height = template_image.rows();
                s.type = (Type) (type);
                s.confidence = template_result[i][j]/max;
                s.pitch = pitch_detect(v,i+(template_image.rows()/3));
                symbols.push_back(s);
                
            }
        }
    }
    return symbols; //vector of symbols to be highlighted in the input image
}

SDoublePlane resizeImage(SDoublePlane &input_image, int new_row, int new_col)
{
    SDoublePlane output(new_row,new_col);
    double row_scale = new_row/input_image.rows();
    double col_scale = new_col/input_image.cols();
    for (int i=0;i<new_row;i++){
        for (int j=0;j<new_col;j++){
            int x = floor(i/row_scale);
            int y = floor(j/col_scale);
            output[i][j] = input_image[x][y];
        }
    }
    return output;
}

SDoublePlane resizeImageScale(SDoublePlane &input_image, double scale)
{
    int new_row = input_image.rows()*scale;
    int new_col = input_image.cols()*scale;
    SDoublePlane output(new_row,new_col);
    for (int i=0;i<new_row;i++){
        for (int j=0;j<new_col;j++){
            int x = floor(i/scale);
            int y = floor(j/scale);
            output[i][j] = input_image[x][y];
        }
    }
    return output;
}

//
// This main file just outputs a few test images. You'll want to change it to do
//  something more interesting!
//
int main(int argc, char *argv[])
{
    if(!(argc == 2))
    {
        cerr << "usage: " << argv[0] << " input_image" << endl;
        return 1;
    }
    
    string input_filename(argv[1]);
    SDoublePlane input_image= SImageIO::read_png_file(input_filename.c_str());
    
    
    string template1("template1.png");
    SDoublePlane template1_image= SImageIO::read_png_file(template1.c_str());
    
    
    string template2("template2.png");
    SDoublePlane template2_image= SImageIO::read_png_file(template2.c_str());
    
    
    string template3("template3.png");
    SDoublePlane template3_image= SImageIO::read_png_file(template3.c_str());
    
    //SDoublePlane newimage = sobel_gradient_filter(input_image);
    //input_image = reduceNoiseFilter(input_image);
    //SDoublePlane newimage = edge(newimage);
    
    //SDoublePlane hedge = edge(input_image);
    
    //SImageIO::write_png_file("horizontaledges.png", hedge,hedge,hedge);
    
    vector<line> v = line_detect(input_image);
    
    int interval = v[2].row - v[1].row;
    double scale = interval/template1_image.rows();
    
    template1_image = resizeImageScale(template1_image,scale);
    template2_image = resizeImageScale(template2_image,scale);
    template3_image = resizeImageScale(template3_image,scale);
    
    vector<DetectedSymbol> symbols;
    
    symbols = detectTemplate(input_image, template1_image, 128, symbols, 0.87, 0,v);
    symbols = detectTemplate(input_image, template2_image, 128, symbols, 0.99, 1,v);
    symbols = detectTemplate(input_image, template3_image, 128, symbols, 0.99, 2,v);
    
    write_detection_txt("detected4.txt", symbols);
    write_detection_image("detected4.png", symbols, input_image);
    
    write_detection_txt("detected7.txt", symbols);
    write_detection_image("detected7.png", symbols, input_image);
    
    SDoublePlane sobel = sobel_gradient_filter(input_image);
    
    SImageIO::write_png_file("edges.png", sobel,sobel,sobel);
    
    vector<DetectedSymbol> symbols2;
    
    symbols2 = detectTemplate(sobel, sobel_gradient_filter(template1_image), 128, symbols2, 0.92, 0,v);
    symbols2 = detectTemplate(sobel, sobel_gradient_filter(template2_image), 128, symbols2, 0.99, 1,v);
    symbols2 = detectTemplate(sobel, sobel_gradient_filter(template3_image), 128, symbols2, 0.99, 2,v);
    
    
    write_detection_txt("detected5.txt", symbols2);
    write_detection_image("detected5.png", symbols2, input_image);
    
    // test step 2 by applying mean filters to the input image
    /*SDoublePlane mean_filter(3,3);
     for(int i=0; i<3; i++)
     for(int j=0; j<3; j++)
     mean_filter[i][j] = 1/9.0;*/
    
}
