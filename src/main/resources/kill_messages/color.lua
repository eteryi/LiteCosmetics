color_pos = {
      'dark_red',
      'red',
      'gold',
      'yellow',
      'green',
      'aqua',
      'dark_aqua',
      'blue',
      'light_purple'
  }

function post(str)
    local newStr = ''
    local reverse = false
    local ind = 0
    for i = 1, #str do
        local character = str:sub(i, i)
        local addition = character

        if not (string.byte(string.upper(character)) >= 65 and string.byte(string.upper(character)) <= 90) then
            goto continue
        end
        do
            if ind >= 9 then
                reverse = true
            end
            if ind <= 1 then
                reverse = false
            end

            if reverse then
                ind = ind - 1
            end
            if not reverse then
                ind = ind + 1
            end
            local color_sel = color_pos[ind]
            addition = color[color_sel] .. character
        end
        ::continue::
        newStr = newStr .. addition
    end
    return newStr
end
