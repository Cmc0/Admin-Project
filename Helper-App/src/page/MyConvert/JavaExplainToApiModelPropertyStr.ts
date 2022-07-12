export const JavaExplainToApiModelPropertySourceTemp =
    `    /**
     * 
     */
    private Long updateId;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 启用/禁用
     */
    private Boolean enableFlag;

    /**
     * 乐观锁
     */
    private Integer version;`

export default `setResult(source.replace(/\\/\\*\\*\\n .* \\* (.*)\\n .* \\*\\//g, '@ApiModelProperty(value = "$1")'))`
